import AsyncStorage from '@react-native-async-storage/async-storage';
import Constants from 'expo-constants';

const getHost = () => {
  const hostUri = Constants.expoConfig?.hostUri ?? Constants.manifest?.debuggerHost;
  if (hostUri) return hostUri.split(':')[0];
  return 'localhost';
};
const HOST = getHost();

const AUTH = `http://${HOST}:8081`;
const USER = `http://${HOST}:8082`;
const MEAL = `http://${HOST}:8083`;
const PLAN = `http://${HOST}:8084`;
const ORCH = `http://${HOST}:8085`;

// ── Token refresh logic ──────────────────────────
let onAuthErrorCallback = null;
let refreshPromise = null;

export function setOnAuthError(cb) {
  onAuthErrorCallback = cb;
}

async function doRefresh() {
  if (refreshPromise) return refreshPromise;

  refreshPromise = (async () => {
    const refreshToken = await AsyncStorage.getItem('refreshToken');
    if (!refreshToken) throw new Error('No refresh token');
    const res = await fetch(`${AUTH}/api/v1/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });
    if (!res.ok) throw new Error('Refresh failed');
    const data = await res.json();
    await AsyncStorage.setItem('accessToken', data.accessToken);
    await AsyncStorage.setItem('refreshToken', data.refreshToken);
    return data.accessToken;
  })().finally(() => { refreshPromise = null; });

  return refreshPromise;
}

async function getToken() {
  return AsyncStorage.getItem('accessToken');
}

async function request(base, path, options = {}, _isRetry = false) {
  const token = await getToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  };
  const res = await fetch(`${base}${path}`, { ...options, headers });

  if (res.status === 401 && !_isRetry) {
    try {
      await doRefresh();
    } catch {
      onAuthErrorCallback?.();
      throw { status: 401, message: 'Сессия истекла, войдите снова' };
    }
    return request(base, path, options, true);
  }

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw { status: res.status, message: err.message || 'Ошибка запроса' };
  }
  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

// ── AUTH ────────────────────────────────────────
export const auth = {
  register: (email, password) =>
    request(AUTH, '/api/v1/auth/register', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  login: (email, password) =>
    request(AUTH, '/api/v1/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  refresh: (refreshToken) =>
    request(AUTH, '/api/v1/auth/refresh', {
      method: 'POST',
      body: JSON.stringify({ refreshToken }),
    }),

  me: () => request(AUTH, '/api/v1/auth/me'),

  logout: (refreshToken) =>
    request(AUTH, '/api/v1/auth/logout', {
      method: 'POST',
      body: JSON.stringify({ refreshToken }),
    }),
};

// ── USER ────────────────────────────────────────
export const user = {
  getProfile: () => request(USER, '/api/v1/users/me'),

  updateProfile: (data) =>
    request(USER, '/api/v1/users/me', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  getQuestionnaire: () => request(USER, '/api/v1/users/me/questionnaire'),

  submitQuestionnaire: (data) =>
    request(USER, '/api/v1/users/me/questionnaire', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  getCalorieTargets: () => request(USER, '/api/v1/users/me/calorie-targets'),

  getCookingPrefs: () => request(USER, '/api/v1/users/me/preferences/cooking'),

  updateCookingPrefs: (data) =>
    request(USER, '/api/v1/users/me/preferences/cooking', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  getCuisinePrefs: () => request(USER, '/api/v1/users/me/preferences/cuisine'),

  updateCuisinePrefs: (data) =>
    request(USER, '/api/v1/users/me/preferences/cuisine', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  getIngredientPrefs: () => request(USER, '/api/v1/users/me/preferences/ingredients'),

  updateIngredientPrefs: (data) =>
    request(USER, '/api/v1/users/me/preferences/ingredients', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  getAllergens: () => request(USER, '/api/v1/users/me/allergens'),

  updateAllergens: (data) =>
    request(USER, '/api/v1/users/me/allergens', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
};

// ── DISH REACTIONS ──────────────────────────────
export const dishReactions = {
  getAll: () => request(USER, '/api/v1/users/me/dish-reactions'),

  getOne: (dishId) => request(USER, `/api/v1/users/me/dish-reactions/${dishId}`),

  set: (dishId, reaction) =>
    request(USER, `/api/v1/users/me/dish-reactions/${dishId}`, {
      method: 'PUT',
      body: JSON.stringify({ reaction }),
    }),

  remove: (dishId) =>
    request(USER, `/api/v1/users/me/dish-reactions/${dishId}`, {
      method: 'DELETE',
    }),
};

// ── DISHES ──────────────────────────────────────
export const dishes = {
  list: ({ mealType, minCalories, maxCalories, title, page = 0, size = 20 } = {}) => {
    const p = [`page=${page}`, `size=${size}`];
    if (mealType)     p.push(`mealType=${encodeURIComponent(mealType)}`);
    if (minCalories)  p.push(`minCalories=${encodeURIComponent(minCalories)}`);
    if (maxCalories)  p.push(`maxCalories=${encodeURIComponent(maxCalories)}`);
    if (title)        p.push(`title=${encodeURIComponent(title)}`);
    return request(MEAL, `/api/v1/dishes?${p.join('&')}`);
  },

  getById: (id) => request(MEAL, `/api/v1/dishes/${id}`),
};

// ── MEAL PLANS ──────────────────────────────────
export const mealPlans = {
  getMy: () => request(PLAN, '/api/v1/meal-plans/my'),

  getByDate: (date) => request(PLAN, `/api/v1/meal-plans/my/date/${date}`),

  getById: (id) => request(PLAN, `/api/v1/meal-plans/${id}`),
};

// ── ORCHESTRATOR ─────────────────────────────────
export const orchestrator = {
  generate: (date) =>
    request(ORCH, '/api/v1/orchestrator/plans/generate', {
      method: 'POST',
      body: JSON.stringify({ date }),
    }),

  replace: (mealPlanId, mealSlot, currentDishId) =>
    request(ORCH, '/api/v1/orchestrator/plans/replace', {
      method: 'POST',
      body: JSON.stringify({ mealPlanId, mealSlot, currentDishId }),
    }),

  getTasks: () => request(ORCH, '/api/v1/orchestrator/tasks'),

  getTask: (jobId) => request(ORCH, `/api/v1/orchestrator/tasks/${jobId}`),
};
