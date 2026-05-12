import React, { useState } from 'react';
import {
  View, Text, StyleSheet, ScrollView,
  TouchableOpacity, KeyboardAvoidingView, Platform,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../../context/AuthContext';
import { Button, Input } from '../../components/ui';
import { COLORS, FONTS, RADIUS, SPACING, SHADOW } from '../../constants/theme';

export default function LoginScreen({ navigation }) {
  const { login } = useAuth();
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [showPwd, setShowPwd]   = useState(false);
  const [loading, setLoading]   = useState(false);
  const [error, setError]       = useState('');

  async function handleLogin() {
    if (!email || !password) { setError('Заполните все поля'); return; }
    setError('');
    setLoading(true);
    try {
      await login(email.trim(), password);
    } catch (e) {
      setError(e.message || 'Неверный email или пароль');
    } finally {
      setLoading(false);
    }
  }

  return (
    <KeyboardAvoidingView
      style={{ flex: 1 }}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <ScrollView style={styles.scroll} contentContainerStyle={styles.content} bounces={false}>
        <LinearGradient
          colors={[COLORS.primaryDark, COLORS.primary, '#5ADB83']}
          style={styles.header}
          start={{ x: 0, y: 0 }}
          end={{ x: 1, y: 1 }}
        >
          <View style={styles.logoWrap}>
            <Text style={styles.logoEmoji}>🥗</Text>
          </View>
          <Text style={styles.appName}>Meal Planner</Text>
          <Text style={styles.tagline}>Питайся правильно, живи лучше</Text>
        </LinearGradient>

        <View style={styles.form}>
          <Text style={styles.title}>Добро пожаловать</Text>
          <Text style={styles.subtitle}>Войдите в аккаунт</Text>

          {error ? (
            <View style={styles.errorBox}>
              <Ionicons name="alert-circle" size={16} color={COLORS.error} />
              <Text style={styles.errorBoxText}>{error}</Text>
            </View>
          ) : null}

          <Input
            label="Email"
            value={email}
            onChangeText={setEmail}
            placeholder="you@example.com"
            keyboardType="email-address"
            autoCapitalize="none"
            autoComplete="email"
            leftIcon={<Ionicons name="mail-outline" size={18} color={COLORS.textMuted} />}
          />

          <Input
            label="Пароль"
            value={password}
            onChangeText={setPassword}
            placeholder="Введите пароль"
            secureTextEntry={!showPwd}
            leftIcon={<Ionicons name="lock-closed-outline" size={18} color={COLORS.textMuted} />}
            rightIcon={
              <TouchableOpacity onPress={() => setShowPwd(v => !v)}>
                <Ionicons
                  name={showPwd ? 'eye-off-outline' : 'eye-outline'}
                  size={18}
                  color={COLORS.textMuted}
                />
              </TouchableOpacity>
            }
          />

          <Button
            title="Войти"
            onPress={handleLogin}
            loading={loading}
            style={{ marginTop: SPACING.sm }}
          />

          <View style={styles.divider}>
            <View style={styles.dividerLine} />
            <Text style={styles.dividerText}>или</Text>
            <View style={styles.dividerLine} />
          </View>

          <TouchableOpacity
            style={styles.registerLink}
            onPress={() => navigation.navigate('Register')}
          >
            <Text style={styles.registerText}>Нет аккаунта? </Text>
            <Text style={styles.registerTextBold}>Зарегистрироваться</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  scroll: { flex: 1, backgroundColor: COLORS.background },
  content: { flexGrow: 1 },

  header: {
    paddingTop: 70,
    paddingBottom: 48,
    alignItems: 'center',
    borderBottomLeftRadius: 36,
    borderBottomRightRadius: 36,
  },
  logoWrap: {
    width: 80,
    height: 80,
    backgroundColor: 'rgba(255,255,255,0.25)',
    borderRadius: 24,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: SPACING.md,
  },
  logoEmoji:  { fontSize: 40 },
  appName:    { fontSize: 28, color: '#fff', ...FONTS.bold,    marginBottom: 4 },
  tagline:    { fontSize: 14, color: 'rgba(255,255,255,0.8)', ...FONTS.regular },

  form: {
    flex: 1,
    padding: SPACING.lg,
    paddingTop: SPACING.xl,
  },
  title:    { fontSize: 24, color: COLORS.text, ...FONTS.bold, marginBottom: 4 },
  subtitle: { fontSize: 14, color: COLORS.textMuted, ...FONTS.regular, marginBottom: SPACING.lg },

  errorBox: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#FFF0EF',
    borderRadius: RADIUS.sm,
    padding: SPACING.sm,
    marginBottom: SPACING.md,
    gap: 8,
  },
  errorBoxText: { fontSize: 13, color: COLORS.error, ...FONTS.medium, flex: 1 },

  divider: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: SPACING.lg,
  },
  dividerLine: { flex: 1, height: 1, backgroundColor: COLORS.border },
  dividerText: {
    marginHorizontal: SPACING.md,
    fontSize: 13,
    color: COLORS.textMuted,
    ...FONTS.regular,
  },

  registerLink: { flexDirection: 'row', justifyContent: 'center' },
  registerText: { fontSize: 14, color: COLORS.textMuted },
  registerTextBold: { fontSize: 14, color: COLORS.primary, ...FONTS.semiBold },
});
