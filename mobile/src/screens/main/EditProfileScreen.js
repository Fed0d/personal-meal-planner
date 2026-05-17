import React, { useEffect, useState } from 'react';
import {
  View, Text, StyleSheet, ScrollView, TextInput, TouchableOpacity,
  Switch, ActivityIndicator, Alert, KeyboardAvoidingView, Platform,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { user as userApi } from '../../api';
import { COLORS, FONTS, RADIUS, SPACING, SHADOW } from '../../constants/theme';

const GOAL_OPTIONS     = ['LOSE_WEIGHT', 'MAINTAIN_WEIGHT', 'GAIN_WEIGHT'];
const GOAL_LABELS      = { LOSE_WEIGHT: '🎯 Похудение', MAINTAIN_WEIGHT: '⚖️ Поддержание', GAIN_WEIGHT: '💪 Набор массы' };
const ACTIVITY_OPTIONS = ['SEDENTARY', 'LOW', 'MODERATE', 'HIGH', 'VERY_HIGH'];
const ACTIVITY_LABELS  = { SEDENTARY: 'Сидячий', LOW: 'Лёгкий', MODERATE: 'Умеренный', HIGH: 'Активный', VERY_HIGH: 'Очень активный' };
const GENDER_OPTIONS   = ['MALE', 'FEMALE'];
const GENDER_LABELS    = { MALE: '👨 Мужской', FEMALE: '👩 Женский' };

const CUISINES = [
  { key: 'asian',       label: '🍜 Азиатская' },
  { key: 'european',    label: '🥐 Европейская' },
  { key: 'eastern',     label: '🫙 Восточная' },
  { key: 'slavic',      label: '🥟 Славянская' },
  { key: 'american',    label: '🍔 Американская' },
  { key: 'mexican',     label: '🌮 Мексиканская' },
];

const INGREDIENTS = [
  { key: 'fish',         label: '🐟 Рыба' },
  { key: 'seafood',      label: '🦐 Морепродукты' },
  { key: 'pork',         label: '🥩 Свинина' },
  { key: 'beef',         label: '🥩 Говядина' },
  { key: 'chicken',      label: '🍗 Курица' },
  { key: 'cheese',       label: '🧀 Сыр' },
  { key: 'potato',       label: '🥔 Картофель' },
  { key: 'onion',        label: '🧅 Лук' },
  { key: 'garlic',       label: '🧄 Чеснок' },
  { key: 'tomatoes',     label: '🍅 Помидоры' },
  { key: 'liver',        label: '🫀 Печень' },
  { key: 'milk',         label: '🥛 Молоко' },
  { key: 'cottageCheese',label: '🫙 Творог' },
  { key: 'olives',       label: '🫒 Оливки' },
  { key: 'celery',       label: '🌿 Сельдерей' },
  { key: 'cilantro',     label: '🌿 Кинза' },
  { key: 'pumpkin',      label: '🎃 Тыква' },
  { key: 'eggplant',     label: '🍆 Баклажан' },
  { key: 'nuts',         label: '🥜 Орехи' },
];

const ALLERGENS = [
  { key: 'nuts',          label: '🥜 Орехи' },
  { key: 'peanut',        label: '🥜 Арахис' },
  { key: 'dairy',         label: '🥛 Молочное' },
  { key: 'gluten',        label: '🌾 Глютен' },
  { key: 'egg',           label: '🥚 Яйца' },
  { key: 'fish',          label: '🐟 Рыба' },
  { key: 'crustaceans',   label: '🦐 Ракообразные' },
  { key: 'molluscs',      label: '🦑 Моллюски' },
  { key: 'soy',           label: '🫘 Соя' },
  { key: 'sesame',        label: '🌿 Кунжут' },
  { key: 'celery',        label: '🌿 Сельдерей' },
  { key: 'mustard',       label: '🌶 Горчица' },
  { key: 'strawberry',    label: '🍓 Клубника' },
  { key: 'foodAdditives', label: '🧪 Пищевые добавки' },
];

const defaultAllergens = () =>
  Object.fromEntries([...ALLERGENS.map(a => [a.key, false]), ['none', false]]);

const defaultCuisines = () =>
  Object.fromEntries(CUISINES.map(c => [c.key, 5]));

const defaultIngredients = () =>
  Object.fromEntries(INGREDIENTS.map(i => [i.key, 5]));

function SectionTitle({ children }) {
  return <Text style={styles.sectionTitle}>{children}</Text>;
}

function Field({ label, children }) {
  return (
    <View style={styles.field}>
      {label && <Text style={styles.fieldLabel}>{label}</Text>}
      {children}
    </View>
  );
}

function ChipGroup({ options, labels, value, onChange }) {
  return (
    <View style={styles.chipGroup}>
      {options.map(o => {
        const active = value === o;
        return (
          <TouchableOpacity
            key={o}
            style={[styles.chip, active && styles.chipActive]}
            onPress={() => onChange(o)}
          >
            <Text style={[styles.chipText, active && styles.chipTextActive]}>
              {labels[o]}
            </Text>
          </TouchableOpacity>
        );
      })}
    </View>
  );
}

function Stepper({ value, onChange, min = 0, max = 300, step = 5, suffix = '' }) {
  return (
    <View style={styles.stepper}>
      <TouchableOpacity
        style={styles.stepBtn}
        onPress={() => onChange(Math.max(min, value - step))}
      >
        <Ionicons name="remove" size={18} color={COLORS.primary} />
      </TouchableOpacity>
      <Text style={styles.stepValue}>{value}{suffix}</Text>
      <TouchableOpacity
        style={styles.stepBtn}
        onPress={() => onChange(Math.min(max, value + step))}
      >
        <Ionicons name="add" size={18} color={COLORS.primary} />
      </TouchableOpacity>
    </View>
  );
}

function ScoreRow({ label, value, onChange }) {
  return (
    <View style={styles.scoreRow}>
      <Text style={styles.scoreLabel}>{label}</Text>
      <View style={styles.scoreControls}>
        <TouchableOpacity style={styles.scoreBtn} onPress={() => onChange(Math.max(0, value - 1))}>
          <Ionicons name="remove" size={16} color={COLORS.primary} />
        </TouchableOpacity>
        <View style={styles.scoreTrack}>
          <View style={[styles.scoreFill, { width: `${value * 10}%` }]} />
        </View>
        <Text style={styles.scoreNum}>{value}</Text>
        <TouchableOpacity style={styles.scoreBtn} onPress={() => onChange(Math.min(10, value + 1))}>
          <Ionicons name="add" size={16} color={COLORS.primary} />
        </TouchableOpacity>
      </View>
    </View>
  );
}

export default function EditProfileScreen({ navigation }) {
  const [loading, setLoading] = useState(true);
  const [saving,  setSaving]  = useState(false);

  const [firstName,      setFirstName]      = useState('');
  const [lastName,       setLastName]       = useState('');
  const [gender,         setGender]         = useState('MALE');
  const [birthDate,      setBirthDate]      = useState('');
  const [heightCm,       setHeightCm]       = useState('');
  const [weightKg,       setWeightKg]       = useState('');
  const [targetWeightKg, setTargetWeightKg] = useState('');
  const [goalType,       setGoalType]       = useState('MAINTAIN_WEIGHT');
  const [activityLevel,  setActivityLevel]  = useState('MODERATE');

  const [activeTime,  setActiveTime]  = useState(30);
  const [passiveTime, setPassiveTime] = useState(60);

  const [cuisines,     setCuisines]     = useState(defaultCuisines());
  const [ingredients,  setIngredients]  = useState(defaultIngredients());
  const [allergens,    setAllergens]    = useState(defaultAllergens());

  useEffect(() => { loadAll(); }, []);

  async function loadAll() {
    try {
      const [p, c, cu, ing, a] = await Promise.allSettled([
        userApi.getProfile(),
        userApi.getCookingPrefs(),
        userApi.getCuisinePrefs(),
        userApi.getIngredientPrefs(),
        userApi.getAllergens(),
      ]);

      if (p.status === 'fulfilled' && p.value) {
        const v = p.value;
        setFirstName(v.firstName || '');
        setLastName(v.lastName || '');
        setGender(v.gender || 'MALE');
        setBirthDate(v.birthDate || '');
        setHeightCm(v.heightCm != null ? String(v.heightCm) : '');
        setWeightKg(v.weightKg != null ? String(v.weightKg) : '');
        setTargetWeightKg(v.targetWeightKg != null ? String(v.targetWeightKg) : '');
        setGoalType(v.goalType || 'MAINTAIN_WEIGHT');
        setActivityLevel(v.activityLevel || 'MODERATE');
      }

      if (c.status === 'fulfilled' && c.value) {
        setActiveTime(c.value.activeCookingTimeMin || 30);
        setPassiveTime(c.value.passiveCookingTimeMin || 60);
      }

      if (cu.status === 'fulfilled' && cu.value) {
        const base = defaultCuisines();
        const val  = cu.value;
        if (val && typeof val === 'object') {
          Object.keys(base).forEach(k => {
            if (val[k] != null) base[k] = val[k];
          });
        }
        setCuisines(base);
      }

      if (ing.status === 'fulfilled' && ing.value) {
        const base = defaultIngredients();
        const val  = ing.value;
        if (val && typeof val === 'object') {
          Object.keys(base).forEach(k => {
            if (val[k] != null) base[k] = val[k];
          });
        }
        setIngredients(base);
      }

      if (a.status === 'fulfilled' && a.value) {
        setAllergens({ ...defaultAllergens(), ...a.value });
      }
    } finally {
      setLoading(false);
    }
  }

  function toggleAllergen(key) {
    if (key === 'none') {
      const reset = defaultAllergens();
      setAllergens({ ...reset, none: !allergens.none });
    } else {
      setAllergens(prev => ({ ...prev, [key]: !prev[key], none: false }));
    }
  }

  async function handleSave() {
    setSaving(true);
    try {
      await Promise.all([
        userApi.updateProfile({
          firstName:      firstName  || null,
          lastName:       lastName   || null,
          gender,
          birthDate:      birthDate  || null,
          heightCm:       heightCm       ? parseFloat(heightCm)       : null,
          weightKg:       weightKg       ? parseFloat(weightKg)       : null,
          targetWeightKg: targetWeightKg ? parseFloat(targetWeightKg) : null,
          goalType,
          activityLevel,
        }),
        userApi.updateCookingPrefs({
          activeCookingTimeMin:  activeTime,
          passiveCookingTimeMin: passiveTime,
        }),
        userApi.updateCuisinePrefs(cuisines),
        userApi.updateIngredientPrefs(ingredients),
        userApi.updateAllergens(allergens),
      ]);
      Alert.alert('Сохранено', 'Данные профиля обновлены', [
        { text: 'OK', onPress: () => navigation.goBack() },
      ]);
    } catch (e) {
      Alert.alert('Ошибка', e.message || 'Не удалось сохранить данные');
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator color={COLORS.primary} size="large" />
      </View>
    );
  }

  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <LinearGradient colors={[COLORS.primaryDeep, COLORS.primaryDark]} style={styles.header} start={{ x: 0, y: 0 }} end={{ x: 1, y: 1 }}>
        <TouchableOpacity style={styles.back} onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={20} color="#fff" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Редактировать профиль</Text>
      </LinearGradient>

      <ScrollView style={styles.scroll} contentContainerStyle={styles.content} showsVerticalScrollIndicator={false} keyboardShouldPersistTaps="handled">

        {/* ── Personal info ── */}
        <View style={styles.section}>
          <SectionTitle>Личные данные</SectionTitle>

          <View style={styles.row}>
            <View style={{ flex: 1 }}>
              <Field label="Имя">
                <TextInput style={styles.input} value={firstName} onChangeText={setFirstName} placeholder="Имя" placeholderTextColor={COLORS.textMuted} />
              </Field>
            </View>
            <View style={{ flex: 1 }}>
              <Field label="Фамилия">
                <TextInput style={styles.input} value={lastName} onChangeText={setLastName} placeholder="Фамилия" placeholderTextColor={COLORS.textMuted} />
              </Field>
            </View>
          </View>

          <Field label="Дата рождения">
            <TextInput style={styles.input} value={birthDate} onChangeText={setBirthDate} placeholder="ГГГГ-ММ-ДД" placeholderTextColor={COLORS.textMuted} />
          </Field>

          <Field label="Пол">
            <ChipGroup options={GENDER_OPTIONS} labels={GENDER_LABELS} value={gender} onChange={setGender} />
          </Field>

          <View style={styles.row}>
            <View style={{ flex: 1 }}>
              <Field label="Рост (см)">
                <TextInput style={styles.input} value={heightCm} onChangeText={setHeightCm} keyboardType="decimal-pad" placeholder="170" placeholderTextColor={COLORS.textMuted} />
              </Field>
            </View>
            <View style={{ flex: 1 }}>
              <Field label="Вес (кг)">
                <TextInput style={styles.input} value={weightKg} onChangeText={setWeightKg} keyboardType="decimal-pad" placeholder="70" placeholderTextColor={COLORS.textMuted} />
              </Field>
            </View>
            <View style={{ flex: 1 }}>
              <Field label="Цель (кг)">
                <TextInput style={styles.input} value={targetWeightKg} onChangeText={setTargetWeightKg} keyboardType="decimal-pad" placeholder="65" placeholderTextColor={COLORS.textMuted} />
              </Field>
            </View>
          </View>

          <Field label="Цель">
            <ChipGroup options={GOAL_OPTIONS} labels={GOAL_LABELS} value={goalType} onChange={setGoalType} />
          </Field>

          <Field label="Уровень активности">
            <ChipGroup options={ACTIVITY_OPTIONS} labels={ACTIVITY_LABELS} value={activityLevel} onChange={setActivityLevel} />
          </Field>
        </View>

        {/* ── Cooking prefs ── */}
        <View style={styles.section}>
          <SectionTitle>Время приготовления</SectionTitle>

          <View style={styles.cookingCard}>
            <Text style={styles.cookingIcon}>🍳</Text>
            <Text style={styles.cookingLabel}>Активное время</Text>
            <Text style={styles.cookingHint}>Время у плиты</Text>
            <Stepper value={activeTime} onChange={setActiveTime} min={5} max={180} step={5} suffix=" мин" />
          </View>

          <View style={[styles.cookingCard, { marginTop: SPACING.md }]}>
            <Text style={styles.cookingIcon}>⏲️</Text>
            <Text style={styles.cookingLabel}>Пассивное время</Text>
            <Text style={styles.cookingHint}>Тушение, запекание</Text>
            <Stepper value={passiveTime} onChange={setPassiveTime} min={0} max={300} step={5} suffix=" мин" />
          </View>
        </View>

        {/* ── Cuisine prefs ── */}
        <View style={styles.section}>
          <SectionTitle>Предпочтения кухни</SectionTitle>
          <Text style={styles.hint}>Оцени каждую кухню от 0 до 10</Text>
          {CUISINES.map(c => (
            <ScoreRow
              key={c.key}
              label={c.label}
              value={cuisines[c.key] ?? 5}
              onChange={v => setCuisines(prev => ({ ...prev, [c.key]: v }))}
            />
          ))}
        </View>

        {/* ── Ingredient prefs ── */}
        <View style={styles.section}>
          <SectionTitle>Предпочтения продуктов</SectionTitle>
          <Text style={styles.hint}>Оцени каждый продукт от 0 до 10</Text>
          {INGREDIENTS.map(i => (
            <ScoreRow
              key={i.key}
              label={i.label}
              value={ingredients[i.key] ?? 5}
              onChange={v => setIngredients(prev => ({ ...prev, [i.key]: v }))}
            />
          ))}
        </View>

        {/* ── Allergens ── */}
        <View style={styles.section}>
          <SectionTitle>Аллергены</SectionTitle>

          <TouchableOpacity
            style={[styles.allergenRow, allergens.none && styles.allergenRowNone]}
            onPress={() => toggleAllergen('none')}
          >
            <Text style={styles.allergenLabel}>✅ Нет аллергенов</Text>
            <Switch
              value={allergens.none}
              onValueChange={() => toggleAllergen('none')}
              trackColor={{ false: COLORS.border, true: COLORS.primaryLight }}
              thumbColor={allergens.none ? COLORS.primary : '#fff'}
            />
          </TouchableOpacity>

          {!allergens.none && ALLERGENS.map(a => (
            <TouchableOpacity
              key={a.key}
              style={[styles.allergenRow, allergens[a.key] && styles.allergenRowActive]}
              onPress={() => toggleAllergen(a.key)}
            >
              <Text style={styles.allergenLabel}>{a.label}</Text>
              <Switch
                value={allergens[a.key] || false}
                onValueChange={() => toggleAllergen(a.key)}
                trackColor={{ false: COLORS.border, true: '#FFCDD2' }}
                thumbColor={allergens[a.key] ? COLORS.error : '#fff'}
              />
            </TouchableOpacity>
          ))}
        </View>

        {/* ── Save ── */}
        <TouchableOpacity
          style={[styles.saveBtn, saving && styles.saveBtnDisabled]}
          onPress={handleSave}
          disabled={saving}
        >
          {saving
            ? <ActivityIndicator color="#fff" />
            : <Text style={styles.saveBtnText}>Сохранить</Text>
          }
        </TouchableOpacity>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  center: { flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: COLORS.background },

  header: {
    paddingTop: 60,
    paddingBottom: SPACING.lg,
    paddingHorizontal: SPACING.lg,
    flexDirection: 'row',
    alignItems: 'center',
    gap: SPACING.md,
  },
  back: {
    width: 40, height: 40,
    borderRadius: 20,
    backgroundColor: 'rgba(255,255,255,0.2)',
    alignItems: 'center', justifyContent: 'center',
  },
  headerTitle: { fontSize: 20, color: '#fff', ...FONTS.bold },

  scroll:  { flex: 1, backgroundColor: COLORS.background },
  content: { paddingBottom: 60 },

  section: {
    backgroundColor: COLORS.surface,
    borderRadius: RADIUS.lg,
    padding: SPACING.lg,
    margin: SPACING.md,
    marginBottom: 0,
    ...SHADOW.sm,
  },
  sectionTitle: { fontSize: 16, color: COLORS.text, ...FONTS.bold, marginBottom: SPACING.md },

  row: { flexDirection: 'row', gap: SPACING.sm },

  field: { marginBottom: SPACING.md },
  fieldLabel: { fontSize: 12, color: COLORS.textMuted, ...FONTS.medium, marginBottom: 4 },

  input: {
    height: 48,
    backgroundColor: COLORS.background,
    borderRadius: RADIUS.md,
    borderWidth: 1.5,
    borderColor: COLORS.border,
    paddingHorizontal: SPACING.md,
    fontSize: 15,
    color: COLORS.text,
    ...FONTS.regular,
  },

  chipGroup: { flexDirection: 'row', flexWrap: 'wrap', gap: SPACING.sm },
  chip: {
    paddingHorizontal: SPACING.md,
    paddingVertical: SPACING.sm,
    borderRadius: RADIUS.md,
    borderWidth: 1.5,
    borderColor: COLORS.border,
    backgroundColor: COLORS.background,
  },
  chipActive:     { backgroundColor: COLORS.primary, borderColor: COLORS.primary },
  chipText:       { fontSize: 13, color: COLORS.text, ...FONTS.medium },
  chipTextActive: { color: '#fff' },

  cookingCard: {
    backgroundColor: COLORS.background,
    borderRadius: RADIUS.lg,
    padding: SPACING.lg,
    alignItems: 'center',
    borderWidth: 1.5,
    borderColor: COLORS.border,
  },
  cookingIcon:  { fontSize: 36, marginBottom: 6 },
  cookingLabel: { fontSize: 15, color: COLORS.text, ...FONTS.bold, marginBottom: 2 },
  cookingHint:  { fontSize: 12, color: COLORS.textMuted, ...FONTS.regular, marginBottom: SPACING.md },

  stepper: { flexDirection: 'row', alignItems: 'center', gap: SPACING.md },
  stepBtn: {
    width: 40, height: 40, borderRadius: 20,
    backgroundColor: COLORS.primaryLight,
    alignItems: 'center', justifyContent: 'center',
  },
  stepValue: { fontSize: 18, color: COLORS.text, ...FONTS.bold, minWidth: 80, textAlign: 'center' },

  hint: { fontSize: 13, color: COLORS.textMuted, ...FONTS.regular, marginBottom: SPACING.md },

  scoreRow: {
    marginBottom: SPACING.sm,
    backgroundColor: COLORS.background,
    borderRadius: RADIUS.md,
    padding: SPACING.md,
    borderWidth: 1,
    borderColor: COLORS.border,
  },
  scoreLabel:    { fontSize: 13, color: COLORS.text, ...FONTS.semiBold, marginBottom: 8 },
  scoreControls: { flexDirection: 'row', alignItems: 'center', gap: SPACING.sm },
  scoreBtn: {
    width: 30, height: 30, borderRadius: 15,
    backgroundColor: COLORS.primaryLight,
    alignItems: 'center', justifyContent: 'center',
  },
  scoreTrack: {
    flex: 1, height: 6, backgroundColor: COLORS.border,
    borderRadius: RADIUS.full, overflow: 'hidden',
  },
  scoreFill: { height: '100%', backgroundColor: COLORS.primary, borderRadius: RADIUS.full },
  scoreNum:  { fontSize: 15, color: COLORS.primary, ...FONTS.bold, minWidth: 24, textAlign: 'center' },

  allergenRow: {
    flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center',
    backgroundColor: COLORS.background,
    borderRadius: RADIUS.md,
    padding: SPACING.md,
    marginBottom: SPACING.sm,
    borderWidth: 1.5,
    borderColor: COLORS.border,
  },
  allergenRowActive: { borderColor: COLORS.error, backgroundColor: '#FFF8F8' },
  allergenRowNone:   { borderColor: COLORS.primary, backgroundColor: COLORS.primaryLight },
  allergenLabel: { fontSize: 14, color: COLORS.text, ...FONTS.medium },

  saveBtn: {
    margin: SPACING.lg,
    marginTop: SPACING.lg,
    backgroundColor: COLORS.primary,
    borderRadius: RADIUS.lg,
    paddingVertical: 16,
    alignItems: 'center',
    ...SHADOW.md,
  },
  saveBtnDisabled: { opacity: 0.6 },
  saveBtnText: { fontSize: 16, color: '#fff', ...FONTS.bold },
});
