import React, { useState } from 'react';
import {
  View, Text, StyleSheet, ScrollView,
  TouchableOpacity, KeyboardAvoidingView, Platform,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../../context/AuthContext';
import { Button, Input } from '../../components/ui';
import { COLORS, FONTS, RADIUS, SPACING } from '../../constants/theme';

export default function RegisterScreen({ navigation }) {
  const { register } = useAuth();
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm]   = useState('');
  const [showPwd, setShowPwd]   = useState(false);
  const [loading, setLoading]   = useState(false);
  const [error, setError]       = useState('');

  async function handleRegister() {
    if (!email || !password || !confirm) { setError('Заполните все поля'); return; }
    if (password !== confirm) { setError('Пароли не совпадают'); return; }
    if (password.length < 6) { setError('Пароль должен быть от 6 символов'); return; }
    setError('');
    setLoading(true);
    try {
      await register(email.trim(), password);
    } catch (e) {
      setError(e.message || 'Ошибка регистрации');
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
          colors={[COLORS.accent, COLORS.primary]}
          style={styles.header}
          start={{ x: 0, y: 0 }}
          end={{ x: 1, y: 1 }}
        >
          <TouchableOpacity style={styles.back} onPress={() => navigation.goBack()}>
            <Ionicons name="arrow-back" size={22} color="#fff" />
          </TouchableOpacity>
          <View style={styles.logoWrap}>
            <Text style={styles.logoEmoji}>✨</Text>
          </View>
          <Text style={styles.appName}>Создать аккаунт</Text>
          <Text style={styles.tagline}>Начни путь к здоровому питанию</Text>
        </LinearGradient>

        <View style={styles.form}>
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
            leftIcon={<Ionicons name="mail-outline" size={18} color={COLORS.textMuted} />}
          />

          <Input
            label="Пароль"
            value={password}
            onChangeText={setPassword}
            placeholder="Минимум 6 символов"
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

          <Input
            label="Подтвердите пароль"
            value={confirm}
            onChangeText={setConfirm}
            placeholder="Повторите пароль"
            secureTextEntry={!showPwd}
            leftIcon={<Ionicons name="shield-checkmark-outline" size={18} color={COLORS.textMuted} />}
          />

          <Button
            title="Зарегистрироваться"
            onPress={handleRegister}
            loading={loading}
            style={{ marginTop: SPACING.sm }}
          />

          <TouchableOpacity
            style={styles.loginLink}
            onPress={() => navigation.goBack()}
          >
            <Text style={styles.loginText}>Уже есть аккаунт? </Text>
            <Text style={styles.loginTextBold}>Войти</Text>
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
    paddingTop: 60,
    paddingBottom: 48,
    alignItems: 'center',
    borderBottomLeftRadius: 36,
    borderBottomRightRadius: 36,
  },
  back: {
    position: 'absolute',
    top: 56,
    left: SPACING.lg,
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: 'rgba(255,255,255,0.2)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  logoWrap: {
    width: 72,
    height: 72,
    backgroundColor: 'rgba(255,255,255,0.25)',
    borderRadius: 22,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: SPACING.md,
  },
  logoEmoji: { fontSize: 36 },
  appName:   { fontSize: 24, color: '#fff', ...FONTS.bold,    marginBottom: 4 },
  tagline:   { fontSize: 13, color: 'rgba(255,255,255,0.8)', ...FONTS.regular },
  form: { padding: SPACING.lg, paddingTop: SPACING.xl },
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
  loginLink: { flexDirection: 'row', justifyContent: 'center', marginTop: SPACING.lg },
  loginText: { fontSize: 14, color: COLORS.textMuted },
  loginTextBold: { fontSize: 14, color: COLORS.primary, ...FONTS.semiBold },
});
