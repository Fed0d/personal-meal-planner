import React, { useCallback, useState } from 'react';
import {
  View, Text, StyleSheet, ScrollView, Switch, Alert, TouchableOpacity,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { useAuth } from '../../context/AuthContext';
import { user as userApi } from '../../api';
import { Card, Button, MacroBar } from '../../components/ui';
import { COLORS, FONTS, RADIUS, SPACING, SHADOW } from '../../constants/theme';

const GOAL_LABELS = {
  LOSE_WEIGHT:    '🎯 Похудение',
  GAIN_MUSCLE:    '💪 Набор массы',
  MAINTAIN:       '⚖️ Поддержание',
};
const ACTIVITY_LABELS = {
  SEDENTARY: 'Сидячий',
  LIGHT:     'Лёгкий',
  MODERATE:  'Умеренный',
  ACTIVE:    'Активный',
  VERY_ACTIVE:'Очень активный',
};

export default function ProfileScreen({ navigation }) {
  const { user, logout } = useAuth();
  const [profile, setProfile]   = useState(null);
  const [targets, setTargets]   = useState(null);
  const [allergens, setAllergens] = useState(null);

  useFocusEffect(useCallback(() => { loadData(); }, []));

  async function loadData() {
    try {
      const [p, t, a] = await Promise.allSettled([
        userApi.getProfile(),
        userApi.getCalorieTargets(),
        userApi.getAllergens(),
      ]);
      if (p.status === 'fulfilled') setProfile(p.value);
      if (t.status === 'fulfilled') setTargets(t.value);
      if (a.status === 'fulfilled') setAllergens(a.value);
    } catch {}
  }

  function handleLogout() {
    Alert.alert('Выйти из аккаунта', 'Вы уверены?', [
      { text: 'Отмена', style: 'cancel' },
      { text: 'Выйти', style: 'destructive', onPress: logout },
    ]);
  }

  const activeAllergens = allergens
    ? Object.entries(allergens).filter(([k, v]) => v && k !== 'none').map(([k]) => k)
    : [];

  return (
    <ScrollView style={styles.scroll} contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
      {/* Header */}
      <LinearGradient
        colors={[COLORS.primaryDeep, COLORS.primaryDark]}
        style={styles.header}
        start={{ x: 0, y: 0 }} end={{ x: 1, y: 1 }}
      >
        <View style={styles.avatar}>
          <Text style={styles.avatarText}>
            {(user?.email?.[0] || 'U').toUpperCase()}
          </Text>
        </View>
        <Text style={styles.userName}>{profile?.firstName} {profile?.lastName}</Text>
        <Text style={styles.userEmail}>{user?.email}</Text>
        {profile?.goalType && (
          <Text style={styles.goal}>{GOAL_LABELS[profile.goalType] || profile.goalType}</Text>
        )}
        <TouchableOpacity style={styles.editBtn} onPress={() => navigation.navigate('EditProfile')}>
          <Ionicons name="create-outline" size={16} color="#fff" />
          <Text style={styles.editBtnText}>Изменить</Text>
        </TouchableOpacity>
      </LinearGradient>

      {/* Stats */}
      {profile && (
        <View style={styles.statsRow}>
          {[
            { label: 'Рост',  value: profile.heightCm ? `${profile.heightCm} см` : '—' },
            { label: 'Вес',   value: profile.weightKg  ? `${profile.weightKg} кг` : '—' },
            { label: 'Цель',  value: profile.targetWeightKg ? `${profile.targetWeightKg} кг` : '—' },
          ].map(s => (
            <Card key={s.label} style={styles.statCard}>
              <Text style={styles.statValue}>{s.value}</Text>
              <Text style={styles.statLabel}>{s.label}</Text>
            </Card>
          ))}
        </View>
      )}

      {/* Calorie targets */}
      {targets && (() => {
        const cal     = Math.round(targets.targetCalories || 0);
        const protein = Math.round((targets.targetCalories || 0) * 0.3 / 4);
        const fat     = Math.round((targets.targetCalories || 0) * 0.3 / 9);
        const carbs   = Math.round((targets.targetCalories || 0) * 0.4 / 4);
        return (
          <Card style={[styles.section, SHADOW.sm]}>
            <Text style={styles.sectionTitle}>Дневная норма</Text>
            <View style={styles.calorieRow}>
              <Text style={styles.calorieNum}>{cal}</Text>
              <Text style={styles.calorieUnit}>ккал/день</Text>
            </View>
            <MacroBar label="Белки"    value={protein} max={protein || 1} color="#FF6B6B" unit="г" />
            <MacroBar label="Жиры"     value={fat}     max={fat     || 1} color="#FFD93D" unit="г" />
            <MacroBar label="Углеводы" value={carbs}   max={carbs   || 1} color={COLORS.primary} unit="г" />
          </Card>
        );
      })()}

      {/* Activity */}
      {profile?.activityLevel && (
        <Card style={[styles.section, SHADOW.sm]}>
          <Text style={styles.sectionTitle}>Активность</Text>
          <View style={styles.infoRow}>
            <Ionicons name="fitness-outline" size={20} color={COLORS.primary} />
            <Text style={styles.infoText}>{ACTIVITY_LABELS[profile.activityLevel] || profile.activityLevel}</Text>
          </View>
          {profile.birthDate && (
            <View style={styles.infoRow}>
              <Ionicons name="calendar-outline" size={20} color={COLORS.primary} />
              <Text style={styles.infoText}>
                Дата рождения: {new Date(profile.birthDate).toLocaleDateString('ru-RU')}
              </Text>
            </View>
          )}
        </Card>
      )}

      {/* Allergens */}
      <Card style={[styles.section, SHADOW.sm]}>
        <Text style={styles.sectionTitle}>Аллергены</Text>
        {activeAllergens.length > 0 ? (
          <View style={styles.allergensWrap}>
            {activeAllergens.map(a => (
              <View key={a} style={styles.allergenChip}>
                <Text style={styles.allergenText}>{a}</Text>
              </View>
            ))}
          </View>
        ) : (
          <Text style={styles.noAllergens}>✅ Без аллергенов</Text>
        )}
      </Card>

      {/* Logout */}
      <Button
        title="Выйти из аккаунта"
        variant="outline"
        onPress={handleLogout}
        style={[styles.logoutBtn, { borderColor: COLORS.error }]}
      />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scroll:  { flex: 1, backgroundColor: COLORS.background },
  content: { paddingBottom: 100 },

  header: {
    paddingTop: 70,
    paddingBottom: 36,
    alignItems: 'center',
    borderBottomLeftRadius: 28,
    borderBottomRightRadius: 28,
  },
  avatar: {
    width: 80, height: 80,
    borderRadius: 40,
    backgroundColor: 'rgba(255,255,255,0.25)',
    alignItems: 'center', justifyContent: 'center',
    marginBottom: SPACING.sm,
  },
  avatarText: { fontSize: 36, color: '#fff', ...FONTS.bold },
  userName:   { fontSize: 20, color: '#fff', ...FONTS.bold },
  userEmail:  { fontSize: 13, color: 'rgba(255,255,255,0.75)', marginBottom: 8, ...FONTS.regular },
  goal:       {
    fontSize: 13,
    color: '#fff',
    backgroundColor: 'rgba(255,255,255,0.2)',
    borderRadius: RADIUS.full,
    paddingHorizontal: 14,
    paddingVertical: 4,
    ...FONTS.medium,
  },

  statsRow: {
    flexDirection: 'row',
    gap: SPACING.sm,
    paddingHorizontal: SPACING.lg,
    marginTop: SPACING.md,
    marginBottom: SPACING.sm,
  },
  statCard: {
    flex: 1,
    alignItems: 'center',
    paddingVertical: SPACING.md,
    ...SHADOW.sm,
  },
  statValue: { fontSize: 18, color: COLORS.primary, ...FONTS.bold },
  statLabel: { fontSize: 11, color: COLORS.textMuted, marginTop: 2, ...FONTS.medium },

  section:      { marginHorizontal: SPACING.lg, marginTop: SPACING.md },
  sectionTitle: { fontSize: 16, color: COLORS.text, ...FONTS.bold, marginBottom: SPACING.md },

  calorieRow: { flexDirection: 'row', alignItems: 'baseline', marginBottom: SPACING.md, gap: 4 },
  calorieNum:  { fontSize: 36, color: COLORS.primary, ...FONTS.extraBold },
  calorieUnit: { fontSize: 14, color: COLORS.textMuted, ...FONTS.regular },

  infoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: SPACING.sm,
    marginBottom: SPACING.sm,
  },
  infoText: { fontSize: 14, color: COLORS.text, ...FONTS.regular },

  allergensWrap: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  allergenChip: {
    backgroundColor: '#FFF0EF',
    borderRadius: RADIUS.full,
    paddingHorizontal: 12,
    paddingVertical: 5,
    borderWidth: 1,
    borderColor: '#FFCDD2',
  },
  allergenText: { fontSize: 12, color: COLORS.error, ...FONTS.medium },
  noAllergens:  { fontSize: 14, color: COLORS.textMuted, ...FONTS.regular },

  logoutBtn: {
    margin: SPACING.lg,
    borderColor: COLORS.error,
  },

  editBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    marginTop: SPACING.md,
    backgroundColor: 'rgba(255,255,255,0.2)',
    borderRadius: RADIUS.full,
    paddingHorizontal: 16,
    paddingVertical: 6,
  },
  editBtnText: { fontSize: 13, color: '#fff', ...FONTS.medium },
});
