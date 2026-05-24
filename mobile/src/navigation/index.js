import React from 'react';
import { ActivityIndicator, View } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';

import { useAuth } from '../context/AuthContext';
import { COLORS, FONTS } from '../constants/theme';

import LoginScreen          from '../screens/auth/LoginScreen';
import RegisterScreen       from '../screens/auth/RegisterScreen';
import QuestionnaireScreen  from '../screens/onboarding/QuestionnaireScreen';
import HomeScreen           from '../screens/main/HomeScreen';
import MealPlanScreen       from '../screens/main/MealPlanScreen';
import DishesScreen         from '../screens/main/DishesScreen';
import DishDetailScreen     from '../screens/main/DishDetailScreen';
import ProfileScreen        from '../screens/main/ProfileScreen';
import EditProfileScreen    from '../screens/main/EditProfileScreen';
import TasksScreen          from '../screens/main/TasksScreen';
import ChatScreen           from '../screens/main/ChatScreen';

const Stack = createStackNavigator();
const Tab   = createBottomTabNavigator();

function AuthStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="Login"    component={LoginScreen} />
      <Stack.Screen name="Register" component={RegisterScreen} />
    </Stack.Navigator>
  );
}

function ProfileStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="ProfileMain"  component={ProfileScreen} />
      <Stack.Screen name="EditProfile"  component={EditProfileScreen} />
    </Stack.Navigator>
  );
}

function DishesStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="DishesList"  component={DishesScreen} />
      <Stack.Screen name="DishDetail"  component={DishDetailScreen} />
    </Stack.Navigator>
  );
}

function MealPlanStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="MealPlanMain" component={MealPlanScreen} />
      <Stack.Screen name="DishDetail"   component={DishDetailScreen} />
    </Stack.Navigator>
  );
}

function MainTabs() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarActiveTintColor: COLORS.primary,
        tabBarInactiveTintColor: COLORS.textMuted,
        tabBarStyle: {
          backgroundColor: '#fff',
          borderTopColor: COLORS.border,
          borderTopWidth: 1,
          height: 80,
          paddingBottom: 20,
          paddingTop: 8,
        },
        tabBarLabelStyle: { fontSize: 11, ...FONTS.medium },
        tabBarIcon: ({ color, size, focused }) => {
          const icons = {
            Home:     focused ? 'home'               : 'home-outline',
            MealPlan: focused ? 'calendar'           : 'calendar-outline',
            Chat:     focused ? 'sparkles'           : 'sparkles-outline',
            Dishes:   focused ? 'restaurant'         : 'restaurant-outline',
            Tasks:    focused ? 'list'               : 'list-outline',
            Profile:  focused ? 'person-circle'      : 'person-circle-outline',
          };
          return <Ionicons name={icons[route.name] || 'ellipse'} size={22} color={color} />;
        },
      })}
    >
      <Tab.Screen name="Home"     component={HomeScreen}     options={{ tabBarLabel: 'Главная' }} />
      <Tab.Screen name="MealPlan" component={MealPlanStack}  options={{ tabBarLabel: 'Планы' }} />
      <Tab.Screen name="Chat"     component={ChatScreen}     options={{ tabBarLabel: 'Чат' }} />
      <Tab.Screen name="Dishes"   component={DishesStack}    options={{ tabBarLabel: 'Блюда' }} />
      <Tab.Screen name="Tasks"    component={TasksScreen}    options={{ tabBarLabel: 'Задачи' }} />
      <Tab.Screen name="Profile"  component={ProfileStack}   options={{ tabBarLabel: 'Профиль' }} />
    </Tab.Navigator>
  );
}

export default function RootNavigator() {
  const { user, loading, needsQuestionnaire } = useAuth();

  if (loading) {
    return (
      <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: COLORS.background }}>
        <ActivityIndicator color={COLORS.primary} size="large" />
      </View>
    );
  }

  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        {!user ? (
          <Stack.Screen name="Auth"          component={AuthStack} />
        ) : needsQuestionnaire ? (
          <Stack.Screen name="Questionnaire" component={QuestionnaireScreen} />
        ) : (
          <Stack.Screen name="Main"          component={MainTabs} />
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}
