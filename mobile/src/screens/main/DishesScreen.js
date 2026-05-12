import React, { useCallback, useState } from 'react';
import {
  View, Text, StyleSheet, FlatList, TextInput,
  TouchableOpacity, ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { dishes as dishApi } from '../../api';
import { Card, Badge, EmptyState } from '../../components/ui';
import { COLORS, FONTS, RADIUS, SPACING, SHADOW } from '../../constants/theme';

const MEAL_FILTERS = [
  { key: null,        label: 'Все' },
  { key: 'BREAKFAST', label: '🌅 Завтрак' },
  { key: 'LUNCH',     label: '☀️ Обед' },
  { key: 'DINNER',    label: '🌙 Ужин' },
];

const MEAL_COLORS = {
  BREAKFAST: COLORS.breakfast,
  LUNCH:     COLORS.lunch,
  DINNER:    COLORS.dinner,
};

export default function DishesScreen({ navigation }) {
  const [mealType, setMealType] = useState(null);
  const [search, setSearch]     = useState('');
  const [data, setData]         = useState([]);
  const [loading, setLoading]   = useState(false);
  const [page, setPage]         = useState(0);
  const [hasMore, setHasMore]   = useState(true);

  useFocusEffect(useCallback(() => { reload(); }, [mealType]));

  async function reload() {
    setPage(0);
    setLoading(true);
    try {
      const res = await dishApi.list({ mealType, title: search || undefined, page: 0, size: 20 });
      setData(res.content || []);
      setHasMore(!res.last);
    } catch {
      setData([]);
    } finally {
      setLoading(false);
    }
  }

  async function loadMore() {
    if (!hasMore || loading) return;
    const next = page + 1;
    try {
      const res = await dishApi.list({ mealType, title: search || undefined, page: next, size: 20 });
      setData(d => [...d, ...(res.content || [])]);
      setHasMore(!res.last);
      setPage(next);
    } catch {}
  }

  function DishCard({ item }) {
    const color = MEAL_COLORS[item.mealType] || COLORS.primary;
    return (
      <Card style={styles.dishCard} onPress={() => navigation.navigate('DishDetail', { id: item.id })}>
        <View style={[styles.dishThumb, { backgroundColor: color + '18' }]}>
          <Text style={styles.dishEmoji}>
            {item.mealType === 'BREAKFAST' ? '🌅' : item.mealType === 'LUNCH' ? '☀️' : '🌙'}
          </Text>
        </View>
        <View style={styles.dishMeta}>
          <Text style={styles.dishName} numberOfLines={2}>{item.title}</Text>
          <View style={styles.dishBottom}>
            <Badge label={item.mealType} color={color} />
            <Text style={styles.dishCal}>{Math.round(item.calories || 0)} ккал</Text>
          </View>
        </View>
        {item.cuisine && (
          <View style={styles.cuisineTag}>
            <Text style={styles.cuisineText}>{item.cuisine}</Text>
          </View>
        )}
      </Card>
    );
  }

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.title}>Блюда</Text>

        {/* Search */}
        <View style={styles.searchBar}>
          <Ionicons name="search-outline" size={18} color={COLORS.textMuted} style={styles.searchIcon} />
          <TextInput
            style={styles.searchInput}
            value={search}
            onChangeText={setSearch}
            placeholder="Поиск блюда..."
            placeholderTextColor={COLORS.textMuted}
            returnKeyType="search"
            onSubmitEditing={reload}
          />
          {search ? (
            <TouchableOpacity onPress={() => { setSearch(''); reload(); }}>
              <Ionicons name="close-circle" size={18} color={COLORS.textMuted} />
            </TouchableOpacity>
          ) : null}
        </View>

        {/* Filters */}
        <View style={styles.filters}>
          {MEAL_FILTERS.map(f => (
            <TouchableOpacity
              key={String(f.key)}
              style={[styles.filterChip, mealType === f.key && styles.filterChipActive]}
              onPress={() => setMealType(f.key)}
            >
              <Text style={[styles.filterText, mealType === f.key && styles.filterTextActive]}>
                {f.label}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {loading && data.length === 0 ? (
        <ActivityIndicator style={{ marginTop: 60 }} color={COLORS.primary} size="large" />
      ) : (
        <FlatList
          data={data}
          keyExtractor={i => String(i.id)}
          renderItem={({ item }) => <DishCard item={item} />}
          contentContainerStyle={styles.list}
          showsVerticalScrollIndicator={false}
          onEndReached={loadMore}
          onEndReachedThreshold={0.3}
          ListEmptyComponent={
            <EmptyState icon="🍳" title="Блюда не найдены" subtitle="Попробуйте другой фильтр или поиск" />
          }
          ListFooterComponent={
            hasMore && data.length > 0
              ? <ActivityIndicator color={COLORS.primary} style={{ marginVertical: 16 }} />
              : null
          }
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.background },
  header: {
    backgroundColor: COLORS.surface,
    paddingTop: 60,
    paddingHorizontal: SPACING.lg,
    paddingBottom: SPACING.md,
    borderBottomWidth: 1,
    borderBottomColor: COLORS.border,
    ...SHADOW.sm,
  },
  title: { fontSize: 24, color: COLORS.text, ...FONTS.bold, marginBottom: SPACING.md },

  searchBar: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: COLORS.background,
    borderRadius: RADIUS.md,
    borderWidth: 1.5,
    borderColor: COLORS.border,
    paddingHorizontal: SPACING.sm,
    marginBottom: SPACING.md,
  },
  searchIcon:  { marginRight: 6 },
  searchInput: { flex: 1, height: 44, fontSize: 15, color: COLORS.text, ...FONTS.regular },

  filters: { flexDirection: 'row', gap: SPACING.sm },
  filterChip: {
    paddingHorizontal: SPACING.md,
    paddingVertical: 7,
    borderRadius: RADIUS.full,
    backgroundColor: COLORS.background,
    borderWidth: 1.5,
    borderColor: COLORS.border,
  },
  filterChipActive: {
    backgroundColor: COLORS.primary,
    borderColor: COLORS.primary,
  },
  filterText:       { fontSize: 13, color: COLORS.textMuted, ...FONTS.medium },
  filterTextActive: { color: '#fff' },

  list: { padding: SPACING.lg, paddingBottom: 100 },
  dishCard: {
    marginBottom: SPACING.md,
    ...SHADOW.sm,
    padding: SPACING.md,
  },
  dishThumb: {
    width: '100%',
    height: 100,
    borderRadius: RADIUS.md,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: SPACING.sm,
  },
  dishEmoji: { fontSize: 44 },
  dishMeta:  {},
  dishName:  { fontSize: 15, color: COLORS.text, ...FONTS.semiBold, marginBottom: SPACING.sm },
  dishBottom: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  dishCal:   { fontSize: 14, color: COLORS.primary, ...FONTS.bold },
  cuisineTag: {
    position: 'absolute',
    top: SPACING.md,
    right: SPACING.md,
    backgroundColor: 'rgba(255,255,255,0.9)',
    borderRadius: RADIUS.full,
    paddingHorizontal: 8,
    paddingVertical: 3,
  },
  cuisineText: { fontSize: 10, color: COLORS.textSecondary, ...FONTS.medium },
});
