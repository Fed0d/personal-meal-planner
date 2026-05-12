import React, { useCallback, useState } from 'react';
import {
  View, Text, StyleSheet, ScrollView, RefreshControl,
  TouchableOpacity, Alert, ActivityIndicator,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { mealPlans, orchestrator } from '../../api';
import { Card, Button, Badge, EmptyState } from '../../components/ui';
import { COLORS, FONTS, RADIUS, SPACING, SHADOW } from '../../constants/theme';

const SLOTS = [
  { key: 'BREAKFAST', label: 'Завтрак',  emoji: '🌅', color: COLORS.breakfast },
  { key: 'LUNCH',     label: 'Обед',     emoji: '☀️',  color: COLORS.lunch },
  { key: 'DINNER',    label: 'Ужин',     emoji: '🌙', color: COLORS.dinner },
];

function dateStr(d) {
  // shift to Moscow (UTC+3) then read UTC fields so device timezone doesn't matter
  const msk = new Date(d.getTime() + 3 * 60 * 60 * 1000);
  const y   = msk.getUTCFullYear();
  const m   = String(msk.getUTCMonth() + 1).padStart(2, '0');
  const day = String(msk.getUTCDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}

export default function MealPlanScreen({ navigation }) {
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [plan, setPlan]                 = useState(null);
  const [tasks, setTasks]               = useState([]);
  const [loading, setLoading]           = useState(true);
  const [refreshing, setRefreshing]     = useState(false);
  const [generating, setGenerating]     = useState(false);

  useFocusEffect(useCallback(() => { loadAll(selectedDate); }, [selectedDate]));

  async function loadAll(date) {
    setLoading(true);
    try {
      const [planRes, tasksRes] = await Promise.allSettled([
        mealPlans.getByDate(dateStr(date)),
        orchestrator.getTasks(),
      ]);
      setPlan(planRes.status === 'fulfilled' ? planRes.value : null);
      setTasks(tasksRes.status === 'fulfilled' ? tasksRes.value : []);
    } finally {
      setLoading(false);
    }
  }

  async function onRefresh() {
    setRefreshing(true);
    await loadAll(selectedDate);
    setRefreshing(false);
  }

  function changeDate(delta) {
    const d = new Date(selectedDate);
    d.setDate(d.getDate() + delta);
    setSelectedDate(d);
  }

  async function handleGenerate() {
    setGenerating(true);
    try {
      await orchestrator.generate(dateStr(selectedDate));
      Alert.alert('✅ Готово', 'Задача на генерацию создана. Проверьте статус в разделе «Задачи».');
      navigation.navigate('Tasks');
    } catch (e) {
      Alert.alert('Ошибка', e.message);
    } finally {
      setGenerating(false);
    }
  }

  async function handleReplace(slot, dishId) {
    if (!plan) return;
    try {
      await orchestrator.replace(plan.id, slot, dishId);
      Alert.alert('✅ Запрос отправлен', 'Замена блюда в процессе. Проверьте статус в «Задачах».');
      navigation.navigate('Tasks');
    } catch (e) {
      Alert.alert('Ошибка', e.message);
    }
  }

  const isToday  = dateStr(selectedDate) === dateStr(new Date());
  const totalCal = plan ? plan.items.reduce((s, i) => s + (i.calories || 0), 0) : 0;
  const slotItem = (key) => plan?.items?.find(i => i.mealSlot === key);

  const targetDate = dateStr(selectedDate);
  console.log('[MealPlan] targetDate:', targetDate, '| tasks:', JSON.stringify(tasks.map(t => ({ type: t.type, status: t.status, date: t.date }))));
  const hasPendingGeneration = tasks.some(t => {
    if (t.type !== 'GENERATE_MEAL_PLAN') return false;
    if (t.status !== 'PENDING' && t.status !== 'IN_PROGRESS') return false;
    if (t.date) return t.date === targetDate;
    if (t.createdAt) return dateStr(new Date(t.createdAt)) === targetDate;
    return false;
  });
  console.log('[MealPlan] hasPendingGeneration:', hasPendingGeneration);

  return (
    <View style={styles.container}>
      {/* Header */}
      <LinearGradient
        colors={[COLORS.primaryDark, COLORS.primary]}
        style={styles.header}
        start={{ x: 0, y: 0 }} end={{ x: 1, y: 1 }}
      >
        <Text style={styles.headerTitle}>План питания</Text>

        {/* Date nav */}
        <View style={styles.datePicker}>
          <TouchableOpacity onPress={() => changeDate(-1)} style={styles.dateArrow}>
            <Ionicons name="chevron-back" size={20} color="#fff" />
          </TouchableOpacity>
          <View style={styles.dateCenter}>
            <Text style={styles.dateText}>
              {selectedDate.toLocaleDateString('ru-RU', { weekday: 'short', day: 'numeric', month: 'long' })}
            </Text>
            {isToday && <Text style={styles.todayBadge}>Сегодня</Text>}
          </View>
          <TouchableOpacity onPress={() => changeDate(1)} style={styles.dateArrow}>
            <Ionicons name="chevron-forward" size={20} color="#fff" />
          </TouchableOpacity>
        </View>

        {plan && (
          <View style={styles.totalCal}>
            <Text style={styles.totalCalLabel}>Всего калорий</Text>
            <Text style={styles.totalCalNum}>{Math.round(totalCal)} ккал</Text>
          </View>
        )}
      </LinearGradient>

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.content}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={COLORS.primary} />}
        showsVerticalScrollIndicator={false}
      >
        {loading ? (
          <ActivityIndicator color={COLORS.primary} size="large" style={{ marginTop: 60 }} />
        ) : !plan ? (
          <Card style={[styles.emptyCard, SHADOW.md]}>
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
                  title="Нет плана на эту дату"
                  subtitle="Создай персональный план питания"
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
        ) : (
          SLOTS.map(slot => {
            const item = slotItem(slot.key);
            return (
              <Card key={slot.key} style={[styles.slotCard, SHADOW.md]}>
                <LinearGradient
                  colors={[slot.color + '18', slot.color + '08']}
                  style={styles.slotGrad}
                  start={{ x: 0, y: 0 }} end={{ x: 1, y: 0 }}
                >
                  <View style={styles.slotHeader}>
                    <View style={[styles.slotDot, { backgroundColor: slot.color + '25' }]}>
                      <Text style={styles.slotEmoji}>{slot.emoji}</Text>
                    </View>
                    <View style={styles.slotMeta}>
                      <Text style={styles.slotLabel}>{slot.label}</Text>
                      {item && (
                        <Text style={[styles.slotCal, { color: slot.color }]}>
                          {Math.round(item.calories)} ккал
                        </Text>
                      )}
                    </View>
                    {item && (
                      <TouchableOpacity
                        style={styles.replaceBtn}
                        onPress={() => handleReplace(slot.key, item.dishId)}
                      >
                        <Ionicons name="shuffle-outline" size={16} color={slot.color} />
                      </TouchableOpacity>
                    )}
                  </View>

                  {item ? (
                    <TouchableOpacity
                      style={styles.dishRow}
                      activeOpacity={0.75}
                      onPress={() => navigation.navigate('DishDetail', { id: item.dishId })}
                    >
                      <View style={styles.dishThumb}>
                        <Text style={{ fontSize: 28 }}>{slot.emoji}</Text>
                      </View>
                      <View style={styles.dishInfo}>
                        <Text style={styles.dishName}>{item.dishName}</Text>
                        <Badge
                          label={slot.label}
                          color={slot.color}
                          style={{ alignSelf: 'flex-start', marginTop: 4 }}
                        />
                      </View>
                      <Ionicons name="chevron-forward" size={18} color={COLORS.textMuted} />
                    </TouchableOpacity>
                  ) : (
                    <View style={styles.noDish}>
                      <Text style={styles.noDishText}>Блюдо не назначено</Text>
                    </View>
                  )}
                </LinearGradient>
              </Card>
            );
          })
        )}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.background },

  header: {
    paddingTop: 60,
    paddingHorizontal: SPACING.lg,
    paddingBottom: SPACING.lg,
    borderBottomLeftRadius: 28,
    borderBottomRightRadius: 28,
  },
  headerTitle: { fontSize: 22, color: '#fff', ...FONTS.bold, marginBottom: SPACING.md },

  datePicker: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(255,255,255,0.18)',
    borderRadius: RADIUS.lg,
    padding: SPACING.sm,
  },
  dateArrow: {
    width: 36, height: 36,
    alignItems: 'center', justifyContent: 'center',
  },
  dateCenter: { flex: 1, alignItems: 'center' },
  dateText:   { fontSize: 15, color: '#fff', ...FONTS.semiBold },
  todayBadge: {
    fontSize: 10,
    color: 'rgba(255,255,255,0.85)',
    backgroundColor: 'rgba(255,255,255,0.2)',
    borderRadius: RADIUS.full,
    paddingHorizontal: 8,
    paddingVertical: 2,
    marginTop: 2,
    ...FONTS.medium,
  },
  totalCal: { alignItems: 'center', marginTop: SPACING.md },
  totalCalLabel: { fontSize: 12, color: 'rgba(255,255,255,0.75)', ...FONTS.regular },
  totalCalNum:   { fontSize: 28, color: '#fff', ...FONTS.extraBold },

  scroll: { flex: 1 },
  content: { padding: SPACING.lg, paddingBottom: 100 },

  emptyCard: { marginTop: SPACING.sm },

  slotCard: { marginBottom: SPACING.md, padding: 0, overflow: 'hidden' },
  slotGrad: { padding: SPACING.md, borderRadius: RADIUS.lg },

  slotHeader: { flexDirection: 'row', alignItems: 'center', marginBottom: SPACING.md },
  slotDot:    { width: 44, height: 44, borderRadius: 14, alignItems: 'center', justifyContent: 'center', marginRight: SPACING.sm },
  slotEmoji:  { fontSize: 22 },
  slotMeta:   { flex: 1 },
  slotLabel:  { fontSize: 16, color: COLORS.text, ...FONTS.semiBold },
  slotCal:    { fontSize: 13, ...FONTS.bold },
  replaceBtn: {
    width: 36, height: 36,
    backgroundColor: 'rgba(255,255,255,0.7)',
    borderRadius: 12,
    alignItems: 'center', justifyContent: 'center',
  },

  dishRow: { flexDirection: 'row', alignItems: 'center' },
  dishThumb: {
    width: 56, height: 56,
    backgroundColor: 'rgba(255,255,255,0.6)',
    borderRadius: RADIUS.md,
    alignItems: 'center', justifyContent: 'center',
    marginRight: SPACING.md,
  },
  dishInfo: { flex: 1 },
  dishName: { fontSize: 14, color: COLORS.text, ...FONTS.semiBold },

  noDish: {
    paddingVertical: SPACING.sm,
    alignItems: 'center',
  },
  noDishText: { fontSize: 13, color: COLORS.textMuted, ...FONTS.regular },
});
