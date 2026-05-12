import React, { useEffect, useState } from 'react';
import {
  View, Text, StyleSheet, ScrollView, ActivityIndicator,
  TouchableOpacity, Image,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { dishes as dishApi, dishReactions as reactionsApi } from '../../api';
import Markdown from 'react-native-markdown-display';
import { Badge, MacroBar } from '../../components/ui';
import { COLORS, FONTS, RADIUS, SPACING, SHADOW } from '../../constants/theme';

const MEAL_COLORS = {
  BREAKFAST: COLORS.breakfast,
  LUNCH:     COLORS.lunch,
  DINNER:    COLORS.dinner,
};

const IMAGE_RE = /!\[.*?\]\((https?:\/\/[^)\s]+)\)/g;
function extractImages(text) {
  if (!text) return [];
  const urls = [];
  let m;
  while ((m = IMAGE_RE.exec(text)) !== null) urls.push(m[1]);
  IMAGE_RE.lastIndex = 0;
  return urls;
}

const mdRules = { image: () => null };

export default function DishDetailScreen({ route, navigation }) {
  const { id } = route.params;
  const [dish, setDish]         = useState(null);
  const [reaction, setReaction] = useState(null);
  const [loading, setLoading]   = useState(true);

  useEffect(() => {
    Promise.all([
      dishApi.getById(id),
      reactionsApi.getOne(id).catch(() => null),
    ]).then(([d, r]) => {
      setDish(d);
      setReaction(r?.reaction ?? null);
    }).catch(() => navigation.goBack())
      .finally(() => setLoading(false));
  }, [id]);

  async function toggleReaction(type) {
    if (reaction === type) {
      await reactionsApi.remove(id).catch(() => {});
      setReaction(null);
    } else {
      const res = await reactionsApi.set(id, type).catch(() => null);
      if (res) setReaction(type);
    }
  }

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator color={COLORS.primary} size="large" />
      </View>
    );
  }

  if (!dish) return null;

  const color    = MEAL_COLORS[dish.mealType] || COLORS.primary;
  const maxMacro = Math.max(dish.protein || 0, dish.fat || 0, dish.carbs || 0, 1);

  const images = [
    ...new Set([
      ...(dish.dishImage ? [dish.dishImage] : []),
      ...extractImages(dish.description),
      ...extractImages(dish.recipe),
    ]),
  ];
  const heroImage = images.length > 0 ? images[images.length - 1] : null;

  return (
    <ScrollView style={styles.scroll} contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>

      {/* Hero */}
      {heroImage ? (
        <View style={styles.hero}>
          <Image source={{ uri: heroImage }} style={StyleSheet.absoluteFill} resizeMode="cover" />
          <LinearGradient
            colors={['transparent', 'rgba(0,0,0,0.45)']}
            style={StyleSheet.absoluteFill}
          />
          <TouchableOpacity style={styles.back} onPress={() => navigation.goBack()}>
            <Ionicons name="arrow-back" size={20} color="#fff" />
          </TouchableOpacity>
        </View>
      ) : (
        <LinearGradient
          colors={[color + 'CC', color + '88']}
          style={styles.hero}
          start={{ x: 0, y: 0 }} end={{ x: 1, y: 1 }}
        >
          <TouchableOpacity style={styles.back} onPress={() => navigation.goBack()}>
            <Ionicons name="arrow-back" size={20} color="#fff" />
          </TouchableOpacity>
          <Text style={styles.heroEmoji}>
            {dish.mealType === 'BREAKFAST' ? '🌅' : dish.mealType === 'LUNCH' ? '☀️' : '🌙'}
          </Text>
        </LinearGradient>
      )}

      <View style={styles.body}>
        <View style={styles.titleRow}>
          <Text style={styles.title} numberOfLines={3}>{dish.title}</Text>
          <Badge label={dish.mealType} color={color} />
        </View>

        <View style={styles.reactionRow}>
          <TouchableOpacity
            style={[styles.reactionBtn, reaction === 'LIKE' && styles.reactionLikeActive]}
            onPress={() => toggleReaction('LIKE')}
          >
            <Ionicons
              name={reaction === 'LIKE' ? 'thumbs-up' : 'thumbs-up-outline'}
              size={18}
              color={reaction === 'LIKE' ? '#fff' : COLORS.textSecondary}
            />
            <Text style={[styles.reactionLabel, reaction === 'LIKE' && styles.reactionLabelActive]}>
              Нравится
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.reactionBtn, reaction === 'DISLIKE' && styles.reactionDislikeActive]}
            onPress={() => toggleReaction('DISLIKE')}
          >
            <Ionicons
              name={reaction === 'DISLIKE' ? 'thumbs-down' : 'thumbs-down-outline'}
              size={18}
              color={reaction === 'DISLIKE' ? '#fff' : COLORS.textSecondary}
            />
            <Text style={[styles.reactionLabel, reaction === 'DISLIKE' && styles.reactionDislikeLabel]}>
              Не нравится
            </Text>
          </TouchableOpacity>
        </View>

        {dish.description && (
          <Markdown rules={mdRules} style={mdStyles}>{dish.description}</Markdown>
        )}

        {/* Meta row */}
        <View style={styles.metaRow}>
          {dish.readyIn && (
            <View style={styles.metaItem}>
              <Ionicons name="time-outline" size={16} color={COLORS.primary} />
              <Text style={styles.metaText}>{dish.readyIn}</Text>
            </View>
          )}
          {dish.cuisine && (
            <View style={styles.metaItem}>
              <Ionicons name="earth-outline" size={16} color={COLORS.primary} />
              <Text style={styles.metaText}>{dish.cuisine}</Text>
            </View>
          )}
          {dish.categoryPath && (
            <View style={styles.metaItem}>
              <Ionicons name="pricetag-outline" size={16} color={COLORS.primary} />
              <Text style={styles.metaText} numberOfLines={1}>{dish.categoryPath.split('/').pop()}</Text>
            </View>
          )}
        </View>

        {/* Calories */}
        <View style={styles.calCard}>
          <Text style={styles.calNum}>{Math.round(dish.calories || 0)}</Text>
          <Text style={styles.calUnit}>ккал</Text>
        </View>

        {/* Macros */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Пищевая ценность</Text>
          <MacroBar label="Белки"    value={dish.protein || 0} max={maxMacro} color="#FF6B6B" />
          <MacroBar label="Жиры"     value={dish.fat     || 0} max={maxMacro} color="#FFD93D" />
          <MacroBar label="Углеводы" value={dish.carbs   || 0} max={maxMacro} color={COLORS.primary} />
        </View>

        {/* Ingredients */}
        {dish.ingredients?.length > 0 && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Ингредиенты</Text>
            <View style={styles.ingredientsWrap}>
              {dish.ingredients.map((ing, i) => (
                <View key={i} style={styles.ingredientChip}>
                  <Text style={styles.ingredientText}>{ing}</Text>
                </View>
              ))}
            </View>
          </View>
        )}

        {/* Photo gallery */}
        {images.length > 0 && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Фото</Text>
            <ScrollView
              horizontal
              showsHorizontalScrollIndicator={false}
              contentContainerStyle={styles.galleryScroll}
            >
              {images.map((uri, i) => (
                <Image key={i} source={{ uri }} style={styles.galleryImage} resizeMode="cover" />
              ))}
            </ScrollView>
          </View>
        )}

        {/* Allergens */}
        {dish.allergens?.length > 0 && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Аллергены</Text>
            <View style={styles.ingredientsWrap}>
              {dish.allergens.map((a, i) => (
                <View key={i} style={styles.allergenChip}>
                  <Text style={styles.allergenText}>⚠️ {a}</Text>
                </View>
              ))}
            </View>
          </View>
        )}

        {/* Recipe */}
        {dish.recipe && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Рецепт</Text>
            <Markdown rules={mdRules} style={mdStyles}>{dish.recipe}</Markdown>
          </View>
        )}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scroll:  { flex: 1, backgroundColor: COLORS.background },
  content: { paddingBottom: 60 },
  center:  { flex: 1, alignItems: 'center', justifyContent: 'center' },

  hero: {
    height: 260,
    alignItems: 'center',
    justifyContent: 'center',
    borderBottomLeftRadius: 32,
    borderBottomRightRadius: 32,
    overflow: 'hidden',
  },
  back: {
    position: 'absolute',
    top: 54, left: SPACING.lg,
    width: 40, height: 40,
    backgroundColor: 'rgba(255,255,255,0.25)',
    borderRadius: 20,
    alignItems: 'center', justifyContent: 'center',
  },
  heroEmoji: { fontSize: 72 },

  body:     { padding: SPACING.lg },
  titleRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start', gap: SPACING.sm, marginBottom: SPACING.sm },
  title:    { flex: 1, fontSize: 22, color: COLORS.text, ...FONTS.bold },

  reactionRow: {
    flexDirection: 'row',
    gap: SPACING.sm,
    marginBottom: SPACING.lg,
  },
  reactionBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    paddingHorizontal: SPACING.md,
    paddingVertical: 8,
    borderRadius: RADIUS.full,
    borderWidth: 1.5,
    borderColor: COLORS.border,
    backgroundColor: COLORS.surface,
  },
  reactionLikeActive: {
    backgroundColor: COLORS.primary,
    borderColor: COLORS.primary,
  },
  reactionDislikeActive: {
    backgroundColor: COLORS.error,
    borderColor: COLORS.error,
  },
  reactionLabel: {
    fontSize: 13,
    color: COLORS.textSecondary,
    ...FONTS.medium,
  },
  reactionLabelActive: {
    color: '#fff',
  },
  reactionDislikeLabel: {
    color: '#fff',
  },

  metaRow:  { flexDirection: 'row', flexWrap: 'wrap', gap: SPACING.md, marginBottom: SPACING.lg },
  metaItem: { flexDirection: 'row', alignItems: 'center', gap: 4 },
  metaText: { fontSize: 13, color: COLORS.textSecondary, ...FONTS.medium },

  calCard: {
    backgroundColor: COLORS.primaryLight,
    borderRadius: RADIUS.lg,
    padding: SPACING.lg,
    alignItems: 'center',
    marginBottom: SPACING.lg,
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 6,
  },
  calNum:  { fontSize: 48, color: COLORS.primaryDark, ...FONTS.extraBold },
  calUnit: { fontSize: 18, color: COLORS.primaryDark, alignSelf: 'flex-end', paddingBottom: 8, ...FONTS.medium },

  section:      { marginBottom: SPACING.lg },
  sectionTitle: { fontSize: 16, color: COLORS.text, ...FONTS.bold, marginBottom: SPACING.md },

  ingredientsWrap: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  ingredientChip: {
    backgroundColor: COLORS.primaryLight,
    borderRadius: RADIUS.full,
    paddingHorizontal: 12,
    paddingVertical: 5,
  },
  ingredientText: { fontSize: 12, color: COLORS.primaryDark, ...FONTS.medium },

  allergenChip: {
    backgroundColor: '#FFF0EF',
    borderRadius: RADIUS.full,
    paddingHorizontal: 12,
    paddingVertical: 5,
    borderWidth: 1,
    borderColor: '#FFCDD2',
  },
  allergenText: { fontSize: 12, color: COLORS.error, ...FONTS.medium },

  galleryScroll: { gap: SPACING.sm, paddingRight: SPACING.lg },
  galleryImage: {
    width: 260,
    height: 180,
    borderRadius: RADIUS.lg,
    ...SHADOW.sm,
  },
});

const mdStyles = {
  body:         { fontSize: 14, color: COLORS.textSecondary, lineHeight: 22, ...FONTS.regular },
  heading1:     { fontSize: 18, color: COLORS.text, ...FONTS.bold, marginBottom: 8, marginTop: 4 },
  heading2:     { fontSize: 16, color: COLORS.text, ...FONTS.bold, marginBottom: 6, marginTop: 4 },
  heading3:     { fontSize: 15, color: COLORS.text, ...FONTS.semiBold, marginBottom: 4 },
  strong:       { ...FONTS.bold },
  em:           { fontStyle: 'italic' },
  bullet_list:  { marginBottom: 8 },
  ordered_list: { marginBottom: 8 },
  list_item:    { marginBottom: 4 },
  code_inline:  { backgroundColor: COLORS.primaryLight, borderRadius: 4, paddingHorizontal: 4, fontFamily: 'monospace' },
  fence:        { backgroundColor: COLORS.primaryLight, borderRadius: 8, padding: 12, marginBottom: 8 },
};
