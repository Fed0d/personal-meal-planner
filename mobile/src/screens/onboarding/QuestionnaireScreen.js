import React, { useState, useCallback } from 'react';
import {
  View, Text, StyleSheet, ScrollView, TouchableOpacity,
  TextInput, Switch, KeyboardAvoidingView, Platform, Alert, Keyboard,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { user as userApi } from '../../api';
import { useAuth } from '../../context/AuthContext';
import { Button } from '../../components/ui';
import { COLORS, FONTS, RADIUS, SPACING, SHADOW } from '../../constants/theme';

const TOTAL_STEPS = 5;

const GOALS = [
  { key: 'LOSE_WEIGHT',  label: '🎯 Похудение' },
  { key: 'GAIN_MUSCLE',  label: '💪 Набор массы' },
  { key: 'MAINTAIN',     label: '⚖️ Поддержание' },
];
const ACTIVITIES = [
  { key: 'SEDENTARY',    label: 'Сидячий',        sub: 'Офис, мало движений' },
  { key: 'LIGHT',        label: 'Лёгкий',          sub: '1–2 тренировки/нед.' },
  { key: 'MODERATE',     label: 'Умеренный',       sub: '3–4 тренировки/нед.' },
  { key: 'ACTIVE',       label: 'Активный',        sub: '5+ тренировок/нед.' },
  { key: 'VERY_ACTIVE',  label: 'Очень активный',  sub: 'Физический труд + спорт' },
];
const CUISINES = [
  { key: 'asian',     label: '🍜 Азиатская' },
  { key: 'european',  label: '🥐 Европейская' },
  { key: 'eastern',   label: '🫙 Восточная' },
  { key: 'slavic',    label: '🥟 Славянская' },
  { key: 'american',  label: '🍔 Американская' },
  { key: 'mexican',   label: '🌮 Мексиканская' },
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

function ProgressBar({ step }) {
  return (
    <View style={styles.progressWrap}>
      {Array.from({ length: TOTAL_STEPS }).map((_, i) => (
        <View
          key={i}
          style={[
            styles.progressDot,
            i < step  && styles.progressDotDone,
            i === step - 1 && styles.progressDotActive,
          ]}
        />
      ))}
    </View>
  );
}

function ChipGroup({ options, value, onChange, multi = false }) {
  return (
    <View style={styles.chipGroup}>
      {options.map(o => {
        const active = multi ? (value || []).includes(o.key) : value === o.key;
        return (
          <TouchableOpacity
            key={o.key}
            style={[styles.chip, active && styles.chipActive]}
            onPress={() => {
              if (multi) {
                const arr = value || [];
                onChange(active ? arr.filter(k => k !== o.key) : [...arr, o.key]);
              } else {
                onChange(o.key);
              }
            }}
          >
            <Text style={[styles.chipText, active && styles.chipTextActive]}>
              {o.label}
            </Text>
            {o.sub && <Text style={[styles.chipSub, active && styles.chipSubActive]}>{o.sub}</Text>}
          </TouchableOpacity>
        );
      })}
    </View>
  );
}

function ScoreRow({ label, value, onChange }) {
  return (
    <View style={styles.scoreRow}>
      <Text style={styles.scoreLabel}>{label}</Text>
      <View style={styles.scoreControls}>
        <TouchableOpacity
          style={styles.scoreBtn}
          onPress={() => onChange(Math.max(0, value - 1))}
        >
          <Ionicons name="remove" size={16} color={COLORS.primary} />
        </TouchableOpacity>
        <View style={styles.scoreTrack}>
          <View style={[styles.scoreFill, { width: `${value * 10}%` }]} />
        </View>
        <Text style={styles.scoreNum}>{value}</Text>
        <TouchableOpacity
          style={styles.scoreBtn}
          onPress={() => onChange(Math.min(10, value + 1))}
        >
          <Ionicons name="add" size={16} color={COLORS.primary} />
        </TouchableOpacity>
      </View>
    </View>
  );
}

function NumberInput({ label, value, onChange, placeholder, suffix }) {
  return (
    <View style={styles.numInputWrap}>
      <Text style={styles.numLabel}>{label}</Text>
      <View style={styles.numRow}>
        <TextInput
          style={styles.numInput}
          value={value}
          onChangeText={onChange}
          keyboardType="numeric"
          placeholder={placeholder || '0'}
          placeholderTextColor={COLORS.textMuted}
          maxLength={3}
        />
        {suffix && <Text style={styles.numSuffix}>{suffix}</Text>}
      </View>
    </View>
  );
}


export default function QuestionnaireScreen() {
  const { completeQuestionnaire } = useAuth();
  const [step, setStep]       = useState(1);
  const [loading, setLoading] = useState(false);

  const handleBack = useCallback(() => { Keyboard.dismiss(); setStep(s => s - 1); }, []);
  const handleNext = useCallback(() => { Keyboard.dismiss(); setStep(s => s + 1); }, []);

  // Step 1
  const [gender, setGender]         = useState('MALE');
  const [birthDate, setBirthDate]   = useState('01-01-1995');
  const [height, setHeight]         = useState('170');
  const [weight, setWeight]         = useState('70');
  const [targetWeight, setTarget]   = useState('65');
  const [goalType, setGoal]         = useState('LOSE_WEIGHT');
  const [activity, setActivity]     = useState('MODERATE');

  // Step 2
  const [activeTime, setActiveTime]   = useState('30');
  const [passiveTime, setPassiveTime] = useState('60');

  // Step 3
  const [cuisines, setCuisines] = useState({
    asian: 5, european: 5, eastern: 5, slavic: 5, american: 5, mexican: 5,
  });

  // Step 4
  const [ingredients, setIngredients] = useState({
    fish: 5, seafood: 5, pork: 5, beef: 5, chicken: 5, cheese: 5,
    potato: 5, onion: 5, garlic: 5, tomatoes: 5, liver: 5, milk: 5,
    cottageCheese: 5, olives: 5, celery: 5, cilantro: 5,
    pumpkin: 5, eggplant: 5, nuts: 5,
  });

  // Step 5
  const [allergens, setAllergens] = useState({
    nuts: false, peanut: false, dairy: false, gluten: false, egg: false,
    fish: false, crustaceans: false, molluscs: false, soy: false,
    sesame: false, celery: false, mustard: false, strawberry: false,
    foodAdditives: false, none: false,
  });

  function toggleAllergen(key) {
    if (key === 'none') {
      const allFalse = Object.fromEntries(
        Object.keys(allergens).map(k => [k, false])
      );
      setAllergens({ ...allFalse, none: !allergens.none });
    } else {
      setAllergens(prev => ({ ...prev, [key]: !prev[key], none: false }));
    }
  }

  function handleBirthDateInput(text) {
    const digits = text.replace(/\D/g, '').slice(0, 8);
    let formatted = digits;
    if (digits.length > 4) {
      formatted = digits.slice(0, 2) + '-' + digits.slice(2, 4) + '-' + digits.slice(4);
    } else if (digits.length > 2) {
      formatted = digits.slice(0, 2) + '-' + digits.slice(2);
    }
    setBirthDate(formatted);
  }

  function birthDateForApi() {
    const [dd, mm, yyyy] = birthDate.split('-');
    return `${yyyy}-${mm}-${dd}`;
  }

  async function handleSubmit() {
    setLoading(true);
    try {
      await userApi.submitQuestionnaire({
        gender,
        birthDate: birthDateForApi(),
        heightCm: parseFloat(height) || 170,
        weightKg: parseFloat(weight) || 70,
        targetWeightKg: parseFloat(targetWeight) || 65,
        goalType,
        activityLevel: activity,
        activeCookingTimeMin: parseInt(activeTime) || 30,
        passiveCookingTimeMin: parseInt(passiveTime) || 60,
        cuisinePreferences: cuisines,
        ingredientPreferences: ingredients,
        allergens,
      });
      await completeQuestionnaire();
    } catch (e) {
      Alert.alert('Ошибка', e.message || 'Не удалось сохранить анкету');
    } finally {
      setLoading(false);
    }
  }

  const STEP_TITLES = ['Личные данные', 'Готовка', 'Кухни мира', 'Продукты', 'Аллергены'];

  const stepContent = (
    <>
      {step === 1 && (
        <View>
          <Text style={styles.sectionLabel}>Пол</Text>
          <ChipGroup
            options={[{ key: 'MALE', label: '👨 Мужской' }, { key: 'FEMALE', label: '👩 Женский' }]}
            value={gender} onChange={setGender}
          />
          <Text style={styles.sectionLabel}>Дата рождения</Text>
          <TextInput
            style={styles.textField}
            value={birthDate} onChangeText={handleBirthDateInput}
            placeholder="ДД-ММ-ГГГГ" placeholderTextColor={COLORS.textMuted}
            keyboardType="numeric" maxLength={10}
          />
          <View style={styles.row3}>
            <NumberInput label="Рост" value={height} onChange={setHeight} suffix="см" />
            <NumberInput label="Вес" value={weight} onChange={setWeight} suffix="кг" />
            <NumberInput label="Цель" value={targetWeight} onChange={setTarget} suffix="кг" />
          </View>
          <Text style={styles.sectionLabel}>Цель</Text>
          <ChipGroup options={GOALS} value={goalType} onChange={setGoal} />
          <Text style={styles.sectionLabel}>Активность</Text>
          <ChipGroup options={ACTIVITIES} value={activity} onChange={setActivity} />
        </View>
      )}

      {step === 2 && (
        <View>
          <View style={styles.cookingCard}>
            <Text style={styles.cookingIcon}>🍳</Text>
            <Text style={styles.cookingTitle}>Активное приготовление</Text>
            <Text style={styles.cookingDesc}>Время, которое вы проводите у плиты</Text>
            <View style={styles.cookingInput}>
              <TouchableOpacity style={styles.timeBtn} onPress={() => setActiveTime(v => String(Math.max(5, parseInt(v) - 5)))}>
                <Ionicons name="remove" size={20} color={COLORS.primary} />
              </TouchableOpacity>
              <Text style={styles.timeNum}>{activeTime} мин</Text>
              <TouchableOpacity style={styles.timeBtn} onPress={() => setActiveTime(v => String(Math.min(180, parseInt(v) + 5)))}>
                <Ionicons name="add" size={20} color={COLORS.primary} />
              </TouchableOpacity>
            </View>
          </View>
          <View style={[styles.cookingCard, { marginTop: SPACING.md }]}>
            <Text style={styles.cookingIcon}>⏲️</Text>
            <Text style={styles.cookingTitle}>Пассивное приготовление</Text>
            <Text style={styles.cookingDesc}>Тушение, запекание — без участия</Text>
            <View style={styles.cookingInput}>
              <TouchableOpacity style={styles.timeBtn} onPress={() => setPassiveTime(v => String(Math.max(0, parseInt(v) - 5)))}>
                <Ionicons name="remove" size={20} color={COLORS.primary} />
              </TouchableOpacity>
              <Text style={styles.timeNum}>{passiveTime} мин</Text>
              <TouchableOpacity style={styles.timeBtn} onPress={() => setPassiveTime(v => String(Math.min(300, parseInt(v) + 5)))}>
                <Ionicons name="add" size={20} color={COLORS.primary} />
              </TouchableOpacity>
            </View>
          </View>
        </View>
      )}

      {step === 3 && (
        <View>
          <Text style={styles.stepHint}>Оцени каждую кухню от 0 до 10</Text>
          {CUISINES.map(c => (
            <ScoreRow key={c.key} label={c.label} value={cuisines[c.key]}
              onChange={v => setCuisines(prev => ({ ...prev, [c.key]: v }))} />
          ))}
        </View>
      )}

      {step === 4 && (
        <View>
          <Text style={styles.stepHint}>Оцени каждый продукт от 0 до 10</Text>
          {INGREDIENTS.map(i => (
            <ScoreRow key={i.key} label={i.label} value={ingredients[i.key]}
              onChange={v => setIngredients(prev => ({ ...prev, [i.key]: v }))} />
          ))}
        </View>
      )}

      {step === 5 && (
        <View>
          <Text style={styles.stepHint}>Отметь аллергены, которых следует избегать</Text>
          <TouchableOpacity
            style={[styles.allergenRow, allergens.none && styles.allergenRowActive]}
            onPress={() => toggleAllergen('none')}
          >
            <Text style={styles.allergenLabel}>✅ Нет аллергенов</Text>
            <Switch value={allergens.none} onValueChange={() => toggleAllergen('none')}
              trackColor={{ false: COLORS.border, true: COLORS.primaryLight }}
              thumbColor={allergens.none ? COLORS.primary : '#fff'} />
          </TouchableOpacity>
          {!allergens.none && ALLERGENS.map(a => (
            <TouchableOpacity key={a.key}
              style={[styles.allergenRow, allergens[a.key] && styles.allergenRowActive]}
              onPress={() => toggleAllergen(a.key)}
            >
              <Text style={styles.allergenLabel}>{a.label}</Text>
              <Switch value={allergens[a.key]} onValueChange={() => toggleAllergen(a.key)}
                trackColor={{ false: COLORS.border, true: '#FFCDD2' }}
                thumbColor={allergens[a.key] ? COLORS.error : '#fff'} />
            </TouchableOpacity>
          ))}
        </View>
      )}

      <View style={styles.footer}>
        {step > 1 && (
          <Button title="Назад" variant="outline" onPress={handleBack} style={styles.footerBtnBack} />
        )}
        {step < TOTAL_STEPS ? (
          <Button title="Далее →" onPress={handleNext} style={styles.footerBtnNext} />
        ) : (
          <Button title="Готово ✓" onPress={handleSubmit} loading={loading} style={styles.footerBtnNext} />
        )}
      </View>
    </>
  );

  const header = (
    <LinearGradient
      colors={[COLORS.primaryDark, COLORS.primary]}
      style={styles.header}
      start={{ x: 0, y: 0 }} end={{ x: 1, y: 1 }}
    >
      <Text style={styles.headerStep}>Шаг {step} из {TOTAL_STEPS}</Text>
      <Text style={styles.headerTitle}>{STEP_TITLES[step - 1]}</Text>
      <ProgressBar step={step} />
    </LinearGradient>
  );

  return (
    <KeyboardAvoidingView
      style={Platform.OS === 'web' ? { height: '100vh' } : { flex: 1 }}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      {header}
      <ScrollView style={styles.scroll} contentContainerStyle={styles.content} showsVerticalScrollIndicator={false} keyboardShouldPersistTaps="handled">
        <View style={styles.stepWrap}>
          {stepContent}
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  header: {
    paddingTop: 60,
    paddingBottom: SPACING.lg,
    paddingHorizontal: SPACING.lg,
  },
  headerStep:  { fontSize: 12, color: 'rgba(255,255,255,0.75)', ...FONTS.medium, marginBottom: 4 },
  headerTitle: { fontSize: 22, color: '#fff', ...FONTS.bold, marginBottom: SPACING.md },
  progressWrap: { flexDirection: 'row', gap: 8 },
  progressDot: {
    flex: 1, height: 4, borderRadius: 2,
    backgroundColor: 'rgba(255,255,255,0.3)',
  },
  progressDotDone:   { backgroundColor: '#fff' },
  progressDotActive: { backgroundColor: '#fff' },

  scroll:   { flex: 1, backgroundColor: COLORS.background },
  content:  { paddingBottom: 60 },
  stepWrap: { padding: SPACING.lg },

  sectionLabel: {
    fontSize: 14, color: COLORS.textSecondary, ...FONTS.semiBold,
    marginBottom: SPACING.sm, marginTop: SPACING.md,
  },
  stepHint: {
    fontSize: 14, color: COLORS.textMuted, ...FONTS.regular,
    marginBottom: SPACING.lg,
  },

  // Chips
  chipGroup: { flexDirection: 'row', flexWrap: 'wrap', gap: SPACING.sm },
  chip: {
    paddingHorizontal: SPACING.md, paddingVertical: SPACING.sm,
    borderRadius: RADIUS.md,
    borderWidth: 1.5, borderColor: COLORS.border,
    backgroundColor: COLORS.surface,
    ...SHADOW.sm,
  },
  chipActive:        { backgroundColor: COLORS.primary, borderColor: COLORS.primary },
  chipText:          { fontSize: 13, color: COLORS.text, ...FONTS.medium },
  chipTextActive:    { color: '#fff' },
  chipSub:           { fontSize: 10, color: COLORS.textMuted, marginTop: 2, ...FONTS.regular },
  chipSubActive:     { color: 'rgba(255,255,255,0.8)' },

  // Text field
  textField: {
    height: 50, backgroundColor: COLORS.surface,
    borderRadius: RADIUS.md, borderWidth: 1.5, borderColor: COLORS.border,
    paddingHorizontal: SPACING.md, fontSize: 15, color: COLORS.text,
    ...FONTS.regular, ...SHADOW.sm,
  },

  // Number input row
  row3: { flexDirection: 'row', gap: SPACING.sm, marginTop: SPACING.sm },
  numInputWrap: { flex: 1 },
  numLabel: { fontSize: 12, color: COLORS.textMuted, ...FONTS.medium, marginBottom: 4 },
  numRow: {
    flexDirection: 'row', alignItems: 'center',
    backgroundColor: COLORS.surface, borderRadius: RADIUS.md,
    borderWidth: 1.5, borderColor: COLORS.border, ...SHADOW.sm,
  },
  numInput: {
    flex: 1, height: 46, paddingHorizontal: SPACING.sm,
    fontSize: 15, color: COLORS.text, ...FONTS.bold,
  },
  numSuffix: { paddingRight: SPACING.sm, fontSize: 12, color: COLORS.textMuted, ...FONTS.medium },

  // Cooking cards
  cookingCard: {
    backgroundColor: COLORS.surface, borderRadius: RADIUS.lg,
    padding: SPACING.lg, alignItems: 'center', ...SHADOW.md,
  },
  cookingIcon:  { fontSize: 40, marginBottom: SPACING.sm },
  cookingTitle: { fontSize: 17, color: COLORS.text, ...FONTS.bold, marginBottom: 4 },
  cookingDesc:  { fontSize: 13, color: COLORS.textMuted, ...FONTS.regular, marginBottom: SPACING.md },
  cookingInput: { flexDirection: 'row', alignItems: 'center', gap: SPACING.md },
  timeBtn: {
    width: 44, height: 44, borderRadius: 22,
    backgroundColor: COLORS.primaryLight,
    alignItems: 'center', justifyContent: 'center',
  },
  timeNum: { fontSize: 20, color: COLORS.text, ...FONTS.bold, minWidth: 80, textAlign: 'center' },

  // Score rows
  scoreRow: {
    marginBottom: SPACING.md,
    backgroundColor: COLORS.surface,
    borderRadius: RADIUS.md,
    padding: SPACING.md,
    ...SHADOW.sm,
  },
  scoreLabel: { fontSize: 14, color: COLORS.text, ...FONTS.semiBold, marginBottom: SPACING.sm },
  scoreControls: { flexDirection: 'row', alignItems: 'center', gap: SPACING.sm },
  scoreBtn: {
    width: 32, height: 32, borderRadius: 16,
    backgroundColor: COLORS.primaryLight,
    alignItems: 'center', justifyContent: 'center',
  },
  scoreTrack: {
    flex: 1, height: 6, backgroundColor: COLORS.border,
    borderRadius: RADIUS.full, overflow: 'hidden',
  },
  scoreFill: { height: '100%', backgroundColor: COLORS.primary, borderRadius: RADIUS.full },
  scoreNum:  { fontSize: 16, color: COLORS.primary, ...FONTS.bold, minWidth: 24, textAlign: 'center' },

  // Allergen rows
  allergenRow: {
    flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center',
    backgroundColor: COLORS.surface, borderRadius: RADIUS.md,
    padding: SPACING.md, marginBottom: SPACING.sm,
    borderWidth: 1.5, borderColor: COLORS.border, ...SHADOW.sm,
  },
  allergenRowActive: { borderColor: COLORS.error, backgroundColor: '#FFF8F8' },
  allergenLabel: { fontSize: 14, color: COLORS.text, ...FONTS.medium },

  // Footer (inside ScrollView — no fixed positioning needed)
  footer: {
    flexDirection: 'row', gap: SPACING.sm,
    padding: SPACING.lg, marginTop: SPACING.md,
    backgroundColor: COLORS.surface,
    borderRadius: RADIUS.lg,
    borderWidth: 1, borderColor: COLORS.border,
  },
  footerBtnBack: { flex: 1 },
  footerBtnNext: { flex: 2 },
});

