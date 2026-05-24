import React, { useRef, useState } from 'react';
import {
  View, Text, StyleSheet, ScrollView, TouchableOpacity,
  TextInput, KeyboardAvoidingView, Platform, ActivityIndicator,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';

import { chat } from '../../api';
import { Card } from '../../components/ui';
import { COLORS, FONTS, RADIUS, SPACING, SHADOW } from '../../constants/theme';

function nowTime() {
  const d = new Date();
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
}

const SEVERITY_COLOR = { high: '#FF3B30', medium: '#FF9F0A', low: '#34C759' };
const SEVERITY_RU    = { high: 'критично', medium: 'умеренно', low: 'немного' };

export default function ChatScreen() {
  const [ingredients, setIngredients] = useState([]);
  const [inputValue, setInputValue]   = useState('');
  const [messages, setMessages]       = useState([
    {
      id: 'welcome',
      role: 'assistant',
      kind: 'text',
      text:
        'Привет! Добавь ингредиенты, которые у тебя есть — и я проанализирую их питательность и предложу 3 сбалансированных рецепта.',
      time: nowTime(),
    },
  ]);
  const [loading, setLoading] = useState(false);
  const scrollRef = useRef(null);

  function scrollToBottom() {
    setTimeout(() => scrollRef.current?.scrollToEnd({ animated: true }), 50);
  }

  function addIngredient() {
    const v = inputValue.trim();
    if (!v) return;
    if (ingredients.includes(v.toLowerCase())) {
      setInputValue('');
      return;
    }
    setIngredients(prev => [...prev, v]);
    setInputValue('');
  }

  function removeIngredient(name) {
    setIngredients(prev => prev.filter(x => x !== name));
  }

  async function sendRequest() {
    if (loading) return;
    if (ingredients.length === 0) {
      setMessages(prev => [...prev, {
        id: `err-${Date.now()}`,
        role: 'assistant',
        kind: 'text',
        text: 'Сначала добавь хотя бы один ингредиент.',
        time: nowTime(),
      }]);
      scrollToBottom();
      return;
    }

    const userMessage = {
      id:   `u-${Date.now()}`,
      role: 'user',
      kind: 'text',
      text: `У меня есть: ${ingredients.join(', ')}.`,
      time: nowTime(),
    };
    setMessages(prev => [...prev, userMessage]);
    scrollToBottom();

    setLoading(true);
    try {
      const data = await chat.fromIngredients({ ingredients });

      setMessages(prev => [...prev, {
        id:   `r-${Date.now()}`,
        role: 'assistant',
        kind: 'recommend',
        data,
        time: nowTime(),
      }]);
      setIngredients([]);
    } catch (e) {
      setMessages(prev => [...prev, {
        id:   `e-${Date.now()}`,
        role: 'assistant',
        kind: 'text',
        text: e?.message || 'Что-то пошло не так. Попробуй ещё раз.',
        time: nowTime(),
      }]);
    } finally {
      setLoading(false);
      scrollToBottom();
    }
  }

  return (
    <View style={styles.root}>
      <LinearGradient
        colors={[COLORS.primaryDark, COLORS.primary]}
        style={styles.header}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
      >
        <View style={styles.headerInner}>
          <View style={styles.headerIcon}>
            <Ionicons name="sparkles" size={20} color="#fff" />
          </View>
          <View style={{ flex: 1 }}>
            <Text style={styles.headerTitle}>AI-помощник</Text>
            <Text style={styles.headerSubtitle}>Анализ питательности и 3 рецепта</Text>
          </View>
        </View>
      </LinearGradient>

      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        keyboardVerticalOffset={Platform.OS === 'ios' ? 80 : 0}
      >
        <ScrollView
          ref={scrollRef}
          style={styles.scroll}
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
        >
          {messages.map(m => <MessageBubble key={m.id} message={m} />)}
          {loading && <TypingBubble />}
        </ScrollView>

        <View style={styles.composer}>
          {ingredients.length > 0 && (
            <View style={styles.chipsRow}>
              {ingredients.map(item => (
                <TouchableOpacity
                  key={item}
                  style={styles.chip}
                  onPress={() => removeIngredient(item)}
                  activeOpacity={0.7}
                >
                  <Text style={styles.chipText}>{item}</Text>
                  <Ionicons name="close" size={14} color={COLORS.primaryDark} style={{ marginLeft: 4 }} />
                </TouchableOpacity>
              ))}
            </View>
          )}

          <View style={styles.inputRow}>
            <View style={styles.inputBox}>
              <TextInput
                value={inputValue}
                onChangeText={setInputValue}
                onSubmitEditing={addIngredient}
                placeholder="Например: курица, рис, помидор"
                placeholderTextColor={COLORS.textMuted}
                style={styles.input}
                returnKeyType="done"
                editable={!loading}
              />
              {inputValue.length > 0 && (
                <TouchableOpacity onPress={addIngredient} style={styles.addBtn} activeOpacity={0.7}>
                  <Ionicons name="add" size={20} color={COLORS.primaryDark} />
                </TouchableOpacity>
              )}
            </View>
            <TouchableOpacity
              style={[styles.sendBtn, (loading || ingredients.length === 0) && styles.sendBtnDisabled]}
              onPress={sendRequest}
              disabled={loading || ingredients.length === 0}
              activeOpacity={0.8}
            >
              {loading
                ? <ActivityIndicator color="#fff" />
                : <Ionicons name="send" size={18} color="#fff" />}
            </TouchableOpacity>
          </View>
        </View>
      </KeyboardAvoidingView>
    </View>
  );
}

// ─── Message components ─────────────────────────

function MessageBubble({ message }) {
  if (message.kind === 'recommend') {
    return <RecommendBubble data={message.data} time={message.time} />;
  }
  const isUser = message.role === 'user';
  return (
    <View style={[styles.bubbleRow, isUser ? styles.bubbleRowRight : styles.bubbleRowLeft]}>
      {!isUser && <BotAvatar />}
      <View style={[styles.bubble, isUser ? styles.bubbleUser : styles.bubbleBot]}>
        <Text style={[styles.bubbleText, isUser && styles.bubbleTextUser]}>{message.text}</Text>
        <Text style={[styles.bubbleTime, isUser && styles.bubbleTimeUser]}>{message.time}</Text>
      </View>
    </View>
  );
}

function BotAvatar() {
  return (
    <View style={styles.botAvatar}>
      <Ionicons name="sparkles" size={14} color="#fff" />
    </View>
  );
}

function TypingBubble() {
  return (
    <View style={[styles.bubbleRow, styles.bubbleRowLeft]}>
      <BotAvatar />
      <View style={[styles.bubble, styles.bubbleBot, { paddingVertical: 14 }]}>
        <ActivityIndicator size="small" color={COLORS.primary} />
      </View>
    </View>
  );
}

function RecommendBubble({ data, time }) {
  const { gapReport, recipes, availableIngredients } = data || {};
  const [activeRecipe, setActiveRecipe] = useState(0);

  return (
    <View style={[styles.bubbleRow, styles.bubbleRowLeft]}>
      <BotAvatar />
      <View style={{ flex: 1 }}>
        {/* Nutrition card */}
        {gapReport && <NutritionCard gapReport={gapReport} ingredients={availableIngredients} />}

        {/* Recipe tabs */}
        {recipes?.length > 0 && (
          <>
            <View style={styles.recipeTabs}>
              {recipes.map((r, i) => (
                <TouchableOpacity
                  key={i}
                  style={[styles.recipeTab, activeRecipe === i && styles.recipeTabActive]}
                  onPress={() => setActiveRecipe(i)}
                  activeOpacity={0.8}
                >
                  <Text style={[styles.recipeTabText, activeRecipe === i && styles.recipeTabTextActive]}>
                    {i + 1}. {r.name?.split(' ').slice(0, 2).join(' ')}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
            <RecipeCard recipe={recipes[activeRecipe]} />
          </>
        )}

        <Text style={[styles.bubbleTime, { marginTop: SPACING.xs }]}>{time}</Text>
      </View>
    </View>
  );
}

function NutritionCard({ gapReport, ingredients }) {
  const score = gapReport.balanceScore ?? 0;
  const scoreColor = score >= 70 ? COLORS.success : score >= 40 ? COLORS.warning : COLORS.error;
  const highGaps = (gapReport.gaps || []).filter(g => g.severity === 'high').slice(0, 3);

  return (
    <Card style={styles.nutritionCard}>
      <View style={styles.nutritionHeader}>
        <Ionicons name="nutrition-outline" size={16} color={COLORS.primaryDark} />
        <Text style={styles.nutritionTitle}>Анализ питательности</Text>
      </View>

      {ingredients?.length > 0 && (
        <Text style={styles.nutritionIngredients}>
          Ингредиенты: {ingredients.join(', ')}
        </Text>
      )}

      <View style={styles.scoreRow}>
        <View style={[styles.scoreBadge, { backgroundColor: scoreColor + '20', borderColor: scoreColor }]}>
          <Text style={[styles.scoreNumber, { color: scoreColor }]}>{score}</Text>
          <Text style={[styles.scoreLabel, { color: scoreColor }]}>/100</Text>
        </View>
        <View style={{ flex: 1, marginLeft: SPACING.sm }}>
          <Text style={styles.scoreDesc}>Баланс питательных веществ</Text>
          {gapReport.weaknesses?.slice(0, 2).map((w, i) => (
            <Text key={i} style={styles.weakness} numberOfLines={1}>
              • {w}
            </Text>
          ))}
        </View>
      </View>

      {highGaps.length > 0 && (
        <View style={styles.gapsRow}>
          {highGaps.map((g, i) => (
            <View key={i} style={[styles.gapPill, { backgroundColor: SEVERITY_COLOR[g.severity] + '15' }]}>
              <View style={[styles.gapDot, { backgroundColor: SEVERITY_COLOR[g.severity] }]} />
              <Text style={[styles.gapText, { color: SEVERITY_COLOR[g.severity] }]}>
                {_nutrientRu(g.nutrient)} — {SEVERITY_RU[g.severity]}
              </Text>
            </View>
          ))}
        </View>
      )}
    </Card>
  );
}

function RecipeCard({ recipe }) {
  const [showSteps, setShowSteps] = useState(false);
  if (!recipe) return null;

  return (
    <Card style={styles.recipeCard}>
      <View style={styles.recipeHeader}>
        <Text style={styles.recipeTitle}>{recipe.name}</Text>
        {recipe.style ? (
          <View style={styles.stylePill}>
            <Text style={styles.styleText}>{recipe.style}</Text>
          </View>
        ) : null}
      </View>

      {recipe.description ? (
        <Text style={styles.recipeDesc}>{recipe.description}</Text>
      ) : null}

      <View style={styles.recipeMeta}>
        {recipe.preparationTime ? (
          <View style={styles.metaPill}>
            <Ionicons name="time-outline" size={12} color={COLORS.primaryDark} />
            <Text style={styles.metaText}>{recipe.preparationTime}</Text>
          </View>
        ) : null}
        {recipe.computedBalanceScore != null && (
          <View style={styles.metaPill}>
            <Ionicons name="bar-chart-outline" size={12} color={COLORS.primaryDark} />
            <Text style={styles.metaText}>Баланс {recipe.computedBalanceScore}/100</Text>
          </View>
        )}
      </View>

      {recipe.ingredients?.length > 0 && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Ингредиенты</Text>
          {recipe.ingredients.map((ing, i) => (
            <Text key={i} style={styles.listItem}>• {ing.item} — {ing.amount}</Text>
          ))}
        </View>
      )}

      {recipe.nutritionalHighlights?.length > 0 && (
        <View style={styles.highlightsRow}>
          {recipe.nutritionalHighlights.slice(0, 3).map((h, i) => (
            <View key={i} style={styles.highlightPill}>
              <Text style={styles.highlightText}>{h}</Text>
            </View>
          ))}
        </View>
      )}

      {recipe.instructions?.length > 0 && (
        <TouchableOpacity
          style={styles.toggleSteps}
          onPress={() => setShowSteps(v => !v)}
          activeOpacity={0.7}
        >
          <Text style={styles.toggleStepsText}>
            {showSteps ? 'Скрыть инструкции' : `Показать инструкции (${recipe.instructions.length} шагов)`}
          </Text>
          <Ionicons
            name={showSteps ? 'chevron-up' : 'chevron-down'}
            size={14}
            color={COLORS.primaryDark}
          />
        </TouchableOpacity>
      )}

      {showSteps && recipe.instructions?.map((step, i) => (
        <View key={i} style={styles.stepRow}>
          <View style={styles.stepNum}><Text style={styles.stepNumText}>{i + 1}</Text></View>
          <Text style={styles.stepText}>{step}</Text>
        </View>
      ))}
    </Card>
  );
}

function _nutrientRu(key) {
  const map = {
    vitamin_c: 'Витамин C', vitamin_a: 'Витамин A', vitamin_d: 'Витамин D',
    vitamin_b12: 'Витамин B12', vitamin_k: 'Витамин K', folate: 'Фолат',
    iron: 'Железо', calcium: 'Кальций', magnesium: 'Магний',
    zinc: 'Цинк', potassium: 'Калий', selenium: 'Селен', fiber: 'Клетчатка',
  };
  return map[key] || key;
}

// ─── Styles ─────────────────────────────────────

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: COLORS.background },

  header: {
    paddingTop: 56,
    paddingBottom: 18,
    paddingHorizontal: SPACING.lg,
    borderBottomLeftRadius: 24,
    borderBottomRightRadius: 24,
  },
  headerInner: { flexDirection: 'row', alignItems: 'center' },
  headerIcon: {
    width: 36, height: 36, borderRadius: 12,
    backgroundColor: 'rgba(255,255,255,0.22)',
    alignItems: 'center', justifyContent: 'center',
    marginRight: SPACING.md,
  },
  headerTitle:    { color: '#fff', fontSize: 18, ...FONTS.bold },
  headerSubtitle: { color: 'rgba(255,255,255,0.85)', fontSize: 12, ...FONTS.regular, marginTop: 2 },

  scroll: { flex: 1 },
  scrollContent: {
    paddingHorizontal: SPACING.md,
    paddingTop: SPACING.lg,
    paddingBottom: SPACING.md,
  },

  bubbleRow: { flexDirection: 'row', marginBottom: SPACING.md, alignItems: 'flex-start' },
  bubbleRowLeft:  { justifyContent: 'flex-start' },
  bubbleRowRight: { justifyContent: 'flex-end' },
  botAvatar: {
    width: 28, height: 28, borderRadius: 14,
    backgroundColor: COLORS.primary,
    alignItems: 'center', justifyContent: 'center',
    marginRight: SPACING.sm, marginTop: 4, flexShrink: 0,
  },

  bubble: {
    maxWidth: '78%',
    paddingHorizontal: SPACING.md,
    paddingVertical: 10,
    borderRadius: RADIUS.lg,
    ...SHADOW.sm,
  },
  bubbleBot:      { backgroundColor: COLORS.surface, borderTopLeftRadius: 4 },
  bubbleUser:     { backgroundColor: COLORS.primary, borderTopRightRadius: 4 },
  bubbleText:     { fontSize: 14, color: COLORS.text, ...FONTS.regular, lineHeight: 20 },
  bubbleTextUser: { color: '#fff' },
  bubbleTime:     { fontSize: 10, color: COLORS.textMuted, marginTop: 4, alignSelf: 'flex-end' },
  bubbleTimeUser: { color: 'rgba(255,255,255,0.7)' },

  // Nutrition card
  nutritionCard: { padding: SPACING.md, marginBottom: SPACING.sm, ...SHADOW.sm },
  nutritionHeader: { flexDirection: 'row', alignItems: 'center', marginBottom: 6 },
  nutritionTitle: { fontSize: 13, color: COLORS.primaryDark, ...FONTS.semiBold, marginLeft: 6 },
  nutritionIngredients: {
    fontSize: 11, color: COLORS.textMuted, ...FONTS.regular,
    marginBottom: SPACING.sm, lineHeight: 16,
  },
  scoreRow: { flexDirection: 'row', alignItems: 'center', marginBottom: SPACING.sm },
  scoreBadge: {
    flexDirection: 'row', alignItems: 'baseline',
    paddingHorizontal: 10, paddingVertical: 6,
    borderRadius: RADIUS.md, borderWidth: 1.5,
    marginRight: SPACING.sm,
  },
  scoreNumber: { fontSize: 22, ...FONTS.bold },
  scoreLabel:  { fontSize: 12, ...FONTS.medium, marginLeft: 1 },
  scoreDesc:   { fontSize: 12, color: COLORS.textMuted, ...FONTS.medium, marginBottom: 4 },
  weakness:    { fontSize: 11, color: COLORS.textMuted, ...FONTS.regular, lineHeight: 16 },
  gapsRow:     { flexDirection: 'row', flexWrap: 'wrap', gap: 6 },
  gapPill: {
    flexDirection: 'row', alignItems: 'center',
    paddingHorizontal: 8, paddingVertical: 4,
    borderRadius: RADIUS.full,
  },
  gapDot:  { width: 6, height: 6, borderRadius: 3, marginRight: 5 },
  gapText: { fontSize: 11, ...FONTS.medium },

  // Recipe tabs
  recipeTabs: {
    flexDirection: 'row', marginBottom: SPACING.sm, gap: 6,
  },
  recipeTab: {
    flex: 1,
    paddingVertical: 7, paddingHorizontal: 4,
    borderRadius: RADIUS.sm,
    backgroundColor: COLORS.surfaceSecondary,
    borderWidth: 1, borderColor: COLORS.border,
    alignItems: 'center',
  },
  recipeTabActive: {
    backgroundColor: COLORS.primaryLight,
    borderColor: COLORS.primary,
  },
  recipeTabText: {
    fontSize: 10, color: COLORS.textMuted, ...FONTS.medium,
    textAlign: 'center',
  },
  recipeTabTextActive: { color: COLORS.primaryDeep, ...FONTS.semiBold },

  // Recipe card
  recipeCard: { padding: SPACING.md, marginBottom: SPACING.xs, ...SHADOW.sm },
  recipeHeader: {
    flexDirection: 'row', alignItems: 'flex-start',
    justifyContent: 'space-between', marginBottom: 6,
  },
  recipeTitle: {
    flex: 1, fontSize: 15, color: COLORS.text, ...FONTS.bold,
    marginRight: SPACING.sm, lineHeight: 20,
  },
  stylePill: {
    backgroundColor: COLORS.primaryLight,
    paddingHorizontal: 8, paddingVertical: 3,
    borderRadius: RADIUS.full,
  },
  styleText: { fontSize: 10, color: COLORS.primaryDeep, ...FONTS.semiBold },
  recipeDesc: {
    fontSize: 13, color: COLORS.textSecondary, ...FONTS.regular,
    marginBottom: SPACING.sm, lineHeight: 18,
  },

  recipeMeta: { flexDirection: 'row', flexWrap: 'wrap', gap: 6, marginBottom: SPACING.sm },
  metaPill: {
    flexDirection: 'row', alignItems: 'center',
    backgroundColor: COLORS.primaryLight,
    paddingHorizontal: 8, paddingVertical: 4,
    borderRadius: RADIUS.full,
  },
  metaText: { fontSize: 11, color: COLORS.primaryDeep, ...FONTS.semiBold, marginLeft: 4 },

  section: { marginTop: SPACING.sm },
  sectionTitle: {
    fontSize: 11, color: COLORS.textMuted, ...FONTS.semiBold,
    textTransform: 'uppercase', letterSpacing: 0.4, marginBottom: 6,
  },
  listItem: { fontSize: 13, color: COLORS.text, ...FONTS.regular, lineHeight: 20 },

  highlightsRow: { flexDirection: 'row', flexWrap: 'wrap', gap: 5, marginTop: SPACING.sm },
  highlightPill: {
    backgroundColor: COLORS.primaryLight,
    paddingHorizontal: 8, paddingVertical: 3,
    borderRadius: RADIUS.full,
  },
  highlightText: { fontSize: 11, color: COLORS.primaryDeep, ...FONTS.medium },

  toggleSteps: {
    flexDirection: 'row', alignItems: 'center', justifyContent: 'center',
    marginTop: SPACING.md, paddingVertical: 8,
    borderTopWidth: 1, borderTopColor: COLORS.border,
  },
  toggleStepsText: {
    fontSize: 12, color: COLORS.primaryDark, ...FONTS.semiBold, marginRight: 4,
  },

  stepRow: { flexDirection: 'row', marginTop: SPACING.sm, alignItems: 'flex-start' },
  stepNum: {
    width: 22, height: 22, borderRadius: 11,
    backgroundColor: COLORS.primaryLight,
    alignItems: 'center', justifyContent: 'center',
    marginRight: 8, marginTop: 1, flexShrink: 0,
  },
  stepNumText: { fontSize: 11, color: COLORS.primaryDeep, ...FONTS.bold },
  stepText: { flex: 1, fontSize: 13, color: COLORS.text, ...FONTS.regular, lineHeight: 18 },

  // Composer
  composer: {
    backgroundColor: COLORS.surface,
    borderTopWidth: 1,
    borderTopColor: COLORS.border,
    paddingHorizontal: SPACING.md,
    paddingTop: SPACING.sm,
    paddingBottom: Platform.OS === 'ios' ? SPACING.md : SPACING.sm,
  },
  chipsRow: { flexDirection: 'row', flexWrap: 'wrap', gap: 6, marginBottom: SPACING.sm },
  chip: {
    flexDirection: 'row', alignItems: 'center',
    backgroundColor: COLORS.primaryLight,
    paddingHorizontal: 10, paddingVertical: 5,
    borderRadius: RADIUS.full,
  },
  chipText: { fontSize: 12, color: COLORS.primaryDeep, ...FONTS.medium },

  inputRow: { flexDirection: 'row', alignItems: 'center' },
  inputBox: {
    flex: 1, flexDirection: 'row', alignItems: 'center',
    backgroundColor: COLORS.surfaceSecondary,
    borderRadius: RADIUS.full,
    borderWidth: 1, borderColor: COLORS.border,
    paddingHorizontal: SPACING.md,
    marginRight: SPACING.sm,
    minHeight: 44,
  },
  input: {
    flex: 1, fontSize: 14, color: COLORS.text,
    paddingVertical: Platform.OS === 'ios' ? 10 : 6,
    ...FONTS.regular,
  },
  addBtn: {
    width: 28, height: 28, borderRadius: 14,
    backgroundColor: COLORS.primaryMid,
    alignItems: 'center', justifyContent: 'center',
    marginLeft: 6,
  },
  sendBtn: {
    width: 44, height: 44, borderRadius: 22,
    backgroundColor: COLORS.primary,
    alignItems: 'center', justifyContent: 'center',
    ...SHADOW.md,
  },
  sendBtnDisabled: { backgroundColor: COLORS.textMuted, ...SHADOW.sm },
});
