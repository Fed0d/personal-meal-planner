import React, { useCallback, useState } from 'react';
import {
  View, Text, StyleSheet, FlatList, RefreshControl,
  TouchableOpacity, ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { orchestrator } from '../../api';
import { Card, EmptyState } from '../../components/ui';
import { COLORS, FONTS, RADIUS, SPACING, SHADOW } from '../../constants/theme';

const STATUS_META = {
  PENDING:     { label: 'Ожидает',    color: COLORS.pending,    icon: 'time-outline',              bg: '#FFF8EC' },
  IN_PROGRESS: { label: 'Выполняется', color: COLORS.inProgress, icon: 'sync-outline',              bg: '#EEF4FF' },
  COMPLETED:   { label: 'Выполнено',  color: COLORS.completed,  icon: 'checkmark-circle-outline',  bg: '#EDFBF1' },
  FAILED:      { label: 'Ошибка',     color: COLORS.failed,     icon: 'close-circle-outline',      bg: '#FFF0EF' },
};

const TYPE_META = {
  GENERATE_MEAL_PLAN: { label: 'Генерация плана', emoji: '✨' },
  REPLACE_DISH:       { label: 'Замена блюда',    emoji: '🔄' },
};

export default function TasksScreen() {
  const [tasks, setTasks]         = useState([]);
  const [loading, setLoading]     = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  useFocusEffect(useCallback(() => { loadTasks(); }, []));

  async function loadTasks() {
    setLoading(true);
    try {
      const data = await orchestrator.getTasks();
      setTasks(Array.isArray(data) ? data : []);
    } catch {
      setTasks([]);
    } finally {
      setLoading(false);
    }
  }

  async function onRefresh() {
    setRefreshing(true);
    await loadTasks();
    setRefreshing(false);
  }

  function TaskCard({ item }) {
    const status = STATUS_META[item.status] || STATUS_META.PENDING;
    const type   = TYPE_META[item.type]    || { label: item.type, emoji: '📋' };
    const date   = item.createdAt
      ? new Date(item.createdAt).toLocaleString('ru-RU', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' })
      : '';

    return (
      <Card style={[styles.taskCard, SHADOW.sm]}>
        <View style={[styles.taskIconWrap, { backgroundColor: status.bg }]}>
          <Text style={styles.taskEmoji}>{type.emoji}</Text>
        </View>
        <View style={styles.taskInfo}>
          <Text style={styles.taskType}>{type.label}</Text>
          {date ? <Text style={styles.taskDate}>{date}</Text> : null}
          {item.resultMessage ? (
            <Text style={styles.taskMsg} numberOfLines={2}>{item.resultMessage}</Text>
          ) : null}
        </View>
        <View style={[styles.statusBadge, { backgroundColor: status.color + '18' }]}>
          <Ionicons name={status.icon} size={13} color={status.color} />
          <Text style={[styles.statusText, { color: status.color }]}>{status.label}</Text>
        </View>
      </Card>
    );
  }

  if (loading && tasks.length === 0) {
    return (
      <View style={styles.center}>
        <ActivityIndicator color={COLORS.primary} size="large" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Задачи</Text>
        <TouchableOpacity onPress={loadTasks} style={styles.refreshBtn}>
          <Ionicons name="refresh-outline" size={20} color={COLORS.primary} />
        </TouchableOpacity>
      </View>

      <FlatList
        data={tasks}
        keyExtractor={i => i.id}
        renderItem={({ item }) => <TaskCard item={item} />}
        contentContainerStyle={styles.list}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={COLORS.primary} />
        }
        showsVerticalScrollIndicator={false}
        ListEmptyComponent={
          <EmptyState
            icon="📋"
            title="Задач пока нет"
            subtitle="Запусти генерацию плана питания"
          />
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.background },
  center:    { flex: 1, alignItems: 'center', justifyContent: 'center' },

  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: COLORS.surface,
    paddingTop: 60,
    paddingHorizontal: SPACING.lg,
    paddingBottom: SPACING.md,
    borderBottomWidth: 1,
    borderBottomColor: COLORS.border,
  },
  title: { fontSize: 24, color: COLORS.text, ...FONTS.bold },
  refreshBtn: {
    width: 38, height: 38,
    backgroundColor: COLORS.primaryLight,
    borderRadius: RADIUS.md,
    alignItems: 'center', justifyContent: 'center',
  },

  list: { padding: SPACING.lg, paddingBottom: 100 },
  taskCard: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: SPACING.md,
    gap: SPACING.md,
  },
  taskIconWrap: {
    width: 48, height: 48,
    borderRadius: RADIUS.md,
    alignItems: 'center', justifyContent: 'center',
  },
  taskEmoji: { fontSize: 24 },
  taskInfo:  { flex: 1 },
  taskType:  { fontSize: 14, color: COLORS.text, ...FONTS.semiBold, marginBottom: 2 },
  taskDate:  { fontSize: 11, color: COLORS.textMuted, ...FONTS.regular },
  taskMsg:   { fontSize: 12, color: COLORS.textSecondary, marginTop: 4, ...FONTS.regular },

  statusBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 3,
    paddingHorizontal: 8,
    paddingVertical: 5,
    borderRadius: RADIUS.full,
  },
  statusText: { fontSize: 11, ...FONTS.semiBold },
});
