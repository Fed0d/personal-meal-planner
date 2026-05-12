import React, { useCallback, useEffect, useState } from 'react';
import {
  View, Text, StyleSheet, ScrollView, RefreshControl,
  TouchableOpacity, ActivityIndicator,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { useAuth } from '../../context/AuthContext';
import { user as userApi, mealPlans, orchestrator } from '../../api';
import { Card, MacroBar, Badge, EmptyState, Button } from '../../components/ui';
import { COLORS, FONTS, RADIUS, SPACING, SHADOW } from '../../constants/theme';

function todayStr() {
  const d = new Date();
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}
const TODAY = todayStr();

const SLOT_META = {
  BREAKFAST: { label: 'Завтрак',  emoji: '🌅', color: COLORS.breakfast },
  LUNCH:     { label: 'Обед',     emoji: '☀️',  color: COLORS.lunch },
  DINNER:    { label: 'Ужин',     emoji: '🌙', color: COLORS.dinner },
};

export default function HomeScreen({ navigation }) {
  const { user }          = useAuth();
  const [targets, setTargets]     = useState(null);
  const [plan, setPlan]           = useState(null);
  const [tasks, setTasks]         = useState([]);
  const [refreshing, setRefreshing] = useState(false);
  const [generating, setGenerating] = useState(false);

  useFocusEffect(useCallback(() => { loadData(); }, []));

  async function loadData() {
    try {
      const [t, p, tk] = await Promise.allSettled([
        userApi.getCalorieTargets(),
        mealPlans.getByDate(TODAY),
        orchestrator.getTasks(),
      ]);
      if (t.status === 'fulfilled') setTargets(t.value);
      if (p.status === 'fulfilled') setPlan(p.value);
      else setPlan(null);
      setTasks(tk.status === 'fulfilled' ? tk.value : []);
    } catch {}
  }

  async function onRefresh() {
    setRefreshing(true);
    await loadData();
    setRefreshing(false);
  }

  async function handleGenerate() {
    setGenerating(true);
    try {
      await orchestrator.generate(TODAY);
      navigation.navigate('Tasks');
    } catch (e) {
      alert(e.message || 'Ошибка генерации');
    } finally {
      setGenerating(false);
    }
  }

  const hasPendingGeneration = tasks.some(t => {
    if (t.type !== 'GENERATE_MEAL_PLAN') return false;
    if (t.status !== 'PENDING' && t.status !== 'IN_PROGRESS') return false;
    if (t.date) return t.date === TODAY;
    if (t.createdAt) {
      const d = new Date(t.createdAt);
      const y = d.getFullYear();
      const mo = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${y}-${mo}-${day}` === TODAY;
    }
    return false;
  });

  const consumed = plan
    ? plan.items.reduce((s, i) => s + (i.calories || 0), 0)
    : 0;
  const target = targets?.targetCalories || 2000;
  const pct    = Math.min((consumed / target) * 100, 100);

  const greeting = () => {
    const h = new Date().getHours();
    if (h < 12) return 'Доброе утро';
    if (h < 18) return 'Добрый день';
    return 'Добрый вечер';
  };

  return (
    <ScrollView
      style={styles.scroll}
      contentContainerStyle={styles.content}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={COLORS.primary} />}
      showsVerticalScrollIndicator={false}
    >
      {/* Header */}
      <LinearGradient
        colors={[COLORS.primaryDark, COLORS.primary]}
        style={styles.header}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
      >
        <View>
          <Text style={styles.greeting}>{greeting()} 👋</Text>
          <Text style={styles.username}>{user?.email?.split('@')[0] || 'Пользователь'}</Text>
        </View>
        <TouchableOpacity
          style={styles.notifBtn}
          onPress={() => navigation.navigate('Tasks')}
        >
          <Ionicons name="notifications-outline" size={22} color="#fff" />
        </TouchableOpacity>
      </LinearGradient>

      <View style={styles.body}>
        {/* Calorie card */}
        <Card style={styles.calorieCard}>
          <Text style={styles.cardTitle}>Калории сегодня</Text>
          <View style={styles.calorieRow}>
            <View style={styles.calorieMain}>
              <Text style={styles.calorieNum}>{Math.round(consumed)}</Text>
              <Text style={styles.calorieUnit}>ккал</Text>
              {targets && <Text style={styles.calorieGoal}>из {Math.round(target)}</Text>}
            </View>
            {targets && (
              <View style={styles.macros}>
                <MacroBar label="Белки"  value={consumed * 0.3 / 4}  max={target * 0.3 / 4}  color="#FF6B6B" />
                <MacroBar label="Жиры"   value={consumed * 0.3 / 9}  max={target * 0.3 / 9}  color="#FFD93D" />
                <MacroBar label="Углев." value={consumed * 0.4 / 4}  max={target * 0.4 / 4}  color={COLORS.primary} />
              </View>
            )}
          </View>
        </Card>

        {/* Today's plan */}
        <View style={styles.section}>
          <View style={styles.sectionRow}>
            <Text style={styles.sectionTitle}>План на сегодня</Text>
            <TouchableOpacity onPress={() => navigation.navigate('MealPlan')}>
              <Text style={styles.seeAll}>Все планы →</Text>
            </TouchableOpacity>
          </View>

          {plan ? (
            plan.items.map((item, idx) => {
              const meta = SLOT_META[item.mealSlot] || {};
              return (
                <Card key={idx} style={styles.mealCard}>
                  <View style={[styles.mealDot, { backgroundColor: meta.color + '22' }]}>
                    <Text style={styles.mealEmoji}>{meta.emoji}</Text>
                  </View>
                  <View style={styles.mealInfo}>
                    <Text style={styles.mealSlot}>{meta.label}</Text>
                    <Text style={styles.mealName} numberOfLines={1}>{item.dishName}</Text>
                  </View>
                  <View style={styles.mealCal}>
                    <Text style={styles.mealCalNum}>{Math.round(item.calories)}</Text>
                    <Text style={styles.mealCalUnit}>ккал</Text>
                  </View>
                </Card>
              );
            })
          ) : (
            <Card style={styles.emptyPlan}>
              {hasPendingGeneration ? (
                <EmptyState
                  icon="⏳"
                  title="Генерация в обработке"
                  subtitle="План питания формируется, проверьте позже или обновите страницу"
                />
              ) : (
                <>
                  <EmptyState
                    icon="🍽️"
                    title="Нет плана на сегодня"
                    subtitle="Сгенерируй персональный план питания"
                  />
                  <Button
                    title={generating ? 'Генерируем...' : '✨ Создать план'}
                    onPress={handleGenerate}
                    loading={generating}
                    style={{ marginTop: SPACING.md }}
                  />
                </>
              )}
            </Card>
          )}
        </View>

        {/* Quick actions */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Быстрые действия</Text>
          <View style={styles.quickRow}>
            {[
              { icon: 'restaurant-outline', label: 'Блюда',    screen: 'Dishes' },
              { icon: 'calendar-outline',   label: 'Планы',    screen: 'MealPlan' },
              { icon: 'person-outline',     label: 'Профиль',  screen: 'Profile' },
              { icon: 'list-outline',       label: 'Задачи',   screen: 'Tasks' },
            ].map(q => (
              <TouchableOpacity
                key={q.screen}
                style={styles.quickItem}
                onPress={() => navigation.navigate(q.screen)}
              >
                <View style={styles.quickIcon}>
                  <Ionicons name={q.icon} size={22} color={COLORS.primary} />
                </View>
                <Text style={styles.quickLabel}>{q.label}</Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scroll:   { flex: 1, backgroundColor: COLORS.background },
  content:  { paddingBottom: 100 },

  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: SPACING.lg,
    paddingTop: 60,
    paddingBottom: 28,
    borderBottomLeftRadius: 28,
    borderBottomRightRadius: 28,
  },
  greeting: { fontSize: 14, color: 'rgba(255,255,255,0.8)', ...FONTS.regular },
  username: { fontSize: 22, color: '#fff', ...FONTS.bold },
  notifBtn: {
    width: 42, height: 42,
    backgroundColor: 'rgba(255,255,255,0.2)',
    borderRadius: 21,
    alignItems: 'center', justifyContent: 'center',
  },

  body: { padding: SPACING.lg, marginTop: -SPACING.md },

  calorieCard: { marginBottom: SPACING.lg, ...SHADOW.md },
  cardTitle:   { fontSize: 16, color: COLORS.text, ...FONTS.semiBold, marginBottom: SPACING.md },

  calorieRow:  { flexDirection: 'row', alignItems: 'center' },
  calorieMain: { alignItems: 'center', marginRight: SPACING.lg, minWidth: 80 },
  calorieNum:  { fontSize: 36, color: COLORS.primary, ...FONTS.extraBold },
  calorieUnit: { fontSize: 12, color: COLORS.textMuted, ...FONTS.medium },
  calorieGoal: { fontSize: 11, color: COLORS.textMuted, marginTop: 2, ...FONTS.regular },

  macros: { flex: 1 },

  section:     { marginBottom: SPACING.lg },
  sectionRow:  { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: SPACING.md },
  sectionTitle:{ fontSize: 18, color: COLORS.text, ...FONTS.bold },
  seeAll:      { fontSize: 13, color: COLORS.primary, ...FONTS.medium },

  mealCard: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: SPACING.sm,
    ...SHADOW.sm,
  },
  mealDot: { width: 44, height: 44, borderRadius: 14, alignItems: 'center', justifyContent: 'center', marginRight: SPACING.md },
  mealEmoji: { fontSize: 22 },
  mealInfo: { flex: 1 },
  mealSlot: { fontSize: 11, color: COLORS.textMuted, ...FONTS.medium, marginBottom: 2 },
  mealName: { fontSize: 14, color: COLORS.text, ...FONTS.semiBold },
  mealCal:  { alignItems: 'flex-end' },
  mealCalNum:  { fontSize: 16, color: COLORS.primary, ...FONTS.bold },
  mealCalUnit: { fontSize: 10, color: COLORS.textMuted, ...FONTS.regular },

  emptyPlan: { ...SHADOW.sm },

  quickRow: { flexDirection: 'row', justifyContent: 'space-between' },
  quickItem: { alignItems: 'center', flex: 1 },
  quickIcon: {
    width: 56, height: 56,
    backgroundColor: COLORS.primaryLight,
    borderRadius: RADIUS.md,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 6,
    ...SHADOW.sm,
  },
  quickLabel: { fontSize: 11, color: COLORS.text, ...FONTS.medium },
});
