import React from 'react';
import {
  TouchableOpacity, Text, StyleSheet, ActivityIndicator,
  TextInput, View,
} from 'react-native';
import { COLORS, FONTS, RADIUS, SHADOW, SPACING } from '../constants/theme';

export function Button({ title, onPress, loading, variant = 'primary', style, disabled }) {
  const isPrimary  = variant === 'primary';
  const isOutline  = variant === 'outline';
  const isGhost    = variant === 'ghost';

  return (
    <TouchableOpacity
      onPress={onPress}
      disabled={loading || disabled}
      activeOpacity={0.8}
      style={[
        styles.btn,
        isPrimary  && styles.btnPrimary,
        isOutline  && styles.btnOutline,
        isGhost    && styles.btnGhost,
        (loading || disabled) && styles.btnDisabled,
        style,
      ]}
    >
      {loading
        ? <ActivityIndicator color={isPrimary ? '#fff' : COLORS.primary} />
        : <Text style={[
            styles.btnText,
            isOutline && styles.btnTextOutline,
            isGhost   && styles.btnTextGhost,
          ]}>{title}</Text>
      }
    </TouchableOpacity>
  );
}

export function Input({ label, error, leftIcon, rightIcon, style, inputStyle, ...props }) {
  return (
    <View style={[styles.inputWrapper, style]}>
      {label && <Text style={styles.inputLabel}>{label}</Text>}
      <View style={[styles.inputContainer, error && styles.inputError]}>
        {leftIcon && <View style={styles.inputIcon}>{leftIcon}</View>}
        <TextInput
          style={[styles.input, leftIcon && styles.inputWithLeft, inputStyle]}
          placeholderTextColor={COLORS.textMuted}
          {...props}
        />
        {rightIcon && <View style={styles.inputIconRight}>{rightIcon}</View>}
      </View>
      {error && <Text style={styles.errorText}>{error}</Text>}
    </View>
  );
}

export function Card({ children, style, onPress }) {
  const Wrapper = onPress ? TouchableOpacity : View;
  return (
    <Wrapper
      activeOpacity={0.9}
      onPress={onPress}
      style={[styles.card, SHADOW.sm, style]}
    >
      {children}
    </Wrapper>
  );
}

export function Badge({ label, color = COLORS.primary, textColor = '#fff', style }) {
  return (
    <View style={[styles.badge, { backgroundColor: color + '22' }, style]}>
      <Text style={[styles.badgeText, { color }]}>{label}</Text>
    </View>
  );
}

export function SectionHeader({ title, action, onAction }) {
  return (
    <View style={styles.sectionHeader}>
      <Text style={styles.sectionTitle}>{title}</Text>
      {action && (
        <TouchableOpacity onPress={onAction}>
          <Text style={styles.sectionAction}>{action}</Text>
        </TouchableOpacity>
      )}
    </View>
  );
}

export function MacroBar({ label, value, max, color, unit = 'г' }) {
  const pct = Math.min((value / max) * 100, 100);
  return (
    <View style={styles.macroBar}>
      <View style={styles.macroTop}>
        <Text style={styles.macroLabel}>{label}</Text>
        <Text style={styles.macroValue}>{Math.round(value)}{unit}</Text>
      </View>
      <View style={styles.macroTrack}>
        <View style={[styles.macroFill, { width: `${pct}%`, backgroundColor: color }]} />
      </View>
    </View>
  );
}

export function EmptyState({ icon, title, subtitle }) {
  return (
    <View style={styles.empty}>
      <Text style={styles.emptyIcon}>{icon}</Text>
      <Text style={styles.emptyTitle}>{title}</Text>
      {subtitle && <Text style={styles.emptySubtitle}>{subtitle}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  // Button
  btn: {
    height: 52,
    borderRadius: RADIUS.md,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: SPACING.lg,
  },
  btnPrimary: {
    backgroundColor: COLORS.primary,
    ...SHADOW.md,
  },
  btnOutline: {
    backgroundColor: 'transparent',
    borderWidth: 1.5,
    borderColor: COLORS.primary,
  },
  btnGhost: {
    backgroundColor: COLORS.primaryLight,
  },
  btnDisabled: { opacity: 0.5 },
  btnText: {
    ...FONTS.semiBold,
    fontSize: 16,
    color: COLORS.textInverse,
  },
  btnTextOutline: { color: COLORS.primary },
  btnTextGhost:   { color: COLORS.primaryDark },

  // Input
  inputWrapper: { marginBottom: SPACING.md },
  inputLabel: {
    ...FONTS.medium,
    fontSize: 13,
    color: COLORS.textSecondary,
    marginBottom: 6,
  },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: COLORS.surface,
    borderRadius: RADIUS.md,
    borderWidth: 1.5,
    borderColor: COLORS.border,
    ...SHADOW.sm,
  },
  inputError: { borderColor: COLORS.error },
  input: {
    flex: 1,
    height: 50,
    paddingHorizontal: SPACING.md,
    fontSize: 15,
    color: COLORS.text,
    ...FONTS.regular,
  },
  inputWithLeft: { paddingLeft: 0 },
  inputIcon: { paddingLeft: SPACING.md },
  inputIconRight: { paddingRight: SPACING.md },
  errorText: {
    fontSize: 12,
    color: COLORS.error,
    marginTop: 4,
    ...FONTS.regular,
  },

  // Card
  card: {
    backgroundColor: COLORS.surface,
    borderRadius: RADIUS.lg,
    padding: SPACING.md,
  },

  // Badge
  badge: {
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: RADIUS.full,
  },
  badgeText: {
    fontSize: 11,
    ...FONTS.semiBold,
  },

  // Section header
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: SPACING.md,
  },
  sectionTitle: {
    fontSize: 18,
    color: COLORS.text,
    ...FONTS.bold,
  },
  sectionAction: {
    fontSize: 14,
    color: COLORS.primary,
    ...FONTS.medium,
  },

  // MacroBar
  macroBar: { marginBottom: 10 },
  macroTop: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 4,
  },
  macroLabel: { fontSize: 12, color: COLORS.textMuted, ...FONTS.medium },
  macroValue: { fontSize: 12, color: COLORS.text, ...FONTS.semiBold },
  macroTrack: {
    height: 5,
    backgroundColor: COLORS.border,
    borderRadius: RADIUS.full,
    overflow: 'hidden',
  },
  macroFill: { height: '100%', borderRadius: RADIUS.full },

  // Empty
  empty: { alignItems: 'center', paddingVertical: SPACING.xxl },
  emptyIcon: { fontSize: 48, marginBottom: SPACING.md },
  emptyTitle: {
    fontSize: 17,
    color: COLORS.text,
    ...FONTS.semiBold,
    marginBottom: 6,
  },
  emptySubtitle: {
    fontSize: 14,
    color: COLORS.textMuted,
    textAlign: 'center',
    ...FONTS.regular,
  },
});
