<template>
  <div class="auth-page">
    <!-- Left Panel - Form -->
    <div class="auth-left">
      <div class="auth-left-content">
        <!-- Logo / Brand -->
        <div class="brand-header">
          <div class="brand-logo">
            <img :src="getImageUrl('logo.jpg')" alt="ETAP" class="logo-img" />
            <span class="brand-name">ETAP</span>
          </div>
          <p class="brand-tagline">Gestion des Consommables</p>
        </div>

        <!-- Form Header -->
        <div class="form-header">
          <h1 class="form-title">Bon retour 👋</h1>
          <p class="form-subtitle">Connectez-vous à votre espace personnel</p>
        </div>

        <!-- Error Alert -->
        <div v-if="errorMessage" class="alert alert-error">
          <span class="alert-icon">⚠</span>
          <span>{{ errorMessage }}</span>
          <button class="alert-close" @click="errorMessage = ''">×</button>
        </div>

        <!-- Login Form -->
        <form @submit.prevent="handleLogin" class="auth-form">
          <div class="form-group">
            <label class="form-label">Email</label>
            <div class="input-wrapper" :class="{ 'input-error': emailErrors.length }">
              <span class="input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                  <polyline points="22,6 12,13 2,6"/>
                </svg>
              </span>
              <input
                v-model="email"
                type="email"
                placeholder="votre@email.com"
                class="form-input"
                required
              />
            </div>
            <span v-if="emailErrors.length" class="field-error">{{ emailErrors[0] }}</span>
          </div>

          <div class="form-group">
            <label class="form-label">Mot de passe</label>
            <div class="input-wrapper" :class="{ 'input-error': passwordErrors.length }">
              <span class="input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
              </span>
              <input
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="••••••••"
                class="form-input"
                required
              />
              <button type="button" class="toggle-password" @click="showPassword = !showPassword">
                <svg v-if="!showPassword" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                  <circle cx="12" cy="12" r="3"/>
                </svg>
                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                  <line x1="1" y1="1" x2="23" y2="23"/>
                </svg>
              </button>
            </div>
            <span v-if="passwordErrors.length" class="field-error">{{ passwordErrors[0] }}</span>
          </div>

          <button type="submit" class="submit-btn" :disabled="loading">
            <span v-if="loading" class="spinner"></span>
            <span v-else>Se Connecter</span>
            <svg v-if="!loading" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="5" y1="12" x2="19" y2="12"/>
              <polyline points="12 5 19 12 12 19"/>
            </svg>
          </button>
        </form>

        <!-- Footer Links -->
        <div class="auth-footer">
          <p>Pas encore de compte ? <router-link to="/cree-compte" class="auth-link">Créer un compte</router-link></p>
          <router-link to="/" class="back-link">← Retour à l'accueil</router-link>
        </div>
      </div>
    </div>

    <!-- Right Panel - Image Showcase -->
    <div class="auth-right">
      <div class="image-slideshow">
        <transition name="slide-fade" mode="out-in">
          <img
            :src="getImageUrl(showcaseImages[currentImageIndex].file)"
            :alt="showcaseImages[currentImageIndex].title"
            :key="currentImageIndex"
            class="showcase-image"
          />
        </transition>
        <div class="image-overlay"></div>
        <div class="showcase-content">
          <div class="showcase-badge">ETAP</div>
          <h2 class="showcase-title">{{ showcaseImages[currentImageIndex].title }}</h2>
          <p class="showcase-desc">{{ showcaseImages[currentImageIndex].description }}</p>
          <div class="slideshow-dots">
            <button
              v-for="(img, i) in showcaseImages"
              :key="i"
              class="dot"
              :class="{ active: i === currentImageIndex }"
              @click="currentImageIndex = i"
            ></button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import UserService from '@/services/UserService'

export default {
  data() {
    return {
      email: '',
      password: '',
      showPassword: false,
      loading: false,
      errorMessage: '',
      emailErrors: [],
      passwordErrors: [],
      currentImageIndex: 0,
      imageInterval: null,
      showcaseImages: [
        { file: 'depot.jpg', title: 'Entrepôt & Stockage', description: 'Logistique et gestion de votre dépôt' },
        { file: 'cartouches.jpg', title: 'Cartouches d\'Impression', description: 'Gestion optimisée de vos consommables d\'impression' },
        { file: 'consommables.jpg', title: 'Équipements IT', description: 'Suivi complet de votre matériel informatique' },
        { file: 'czrtouches1.jpg', title: 'Toners & Cartouches', description: 'Inventaire en temps réel de vos produits' }
      ] 
    }
  },
  methods: {
    getImageUrl(name) {
      return new URL(`../assets/${name}`, import.meta.url).href;
    },
    async handleLogin() {
      this.errorMessage = ''
      this.emailErrors = []
      this.passwordErrors = []

      if (!this.email) {
        this.emailErrors = ['L\'email est requis']
        return
      }
      if (!/.+@.+\..+/.test(this.email)) {
        this.emailErrors = ['L\'email doit être valide']
        return
      }
      if (!this.password) {
        this.passwordErrors = ['Le mot de passe est requis']
        return
      }
      if (this.password.length < 6) {
        this.passwordErrors = ['Le mot de passe doit contenir au moins 6 caractères']
        return
      }

      this.loading = true

      try {
        const response = await UserService.login(this.email, this.password);

        if (response.success) {
          const user = response.user;
          if (response.token) {
            localStorage.setItem('jwt_token', response.token);
          }
          localStorage.setItem('user', JSON.stringify(user));
          localStorage.setItem('isAuthenticated', 'true');

          if (user.role === 'admin') {
            this.$router.push('/admin/dashboard');
          } else {
            this.$router.push('/');
          }
        } else {
          this.errorMessage = response.message || 'Email ou mot de passe incorrect';
          this.passwordErrors = ['Mot de passe incorrect'];
        }
      } catch (error) {
        console.error('Login error:', error);
        if (error.response && error.response.status === 401) {
          this.errorMessage = 'Email ou mot de passe incorrect';
          this.passwordErrors = ['Identifiants invalides'];
        } else if (error.response && error.response.status === 404) {
          this.errorMessage = 'Aucun compte associé à cet email';
          this.emailErrors = ['Utilisateur non trouvé'];
        } else {
          this.errorMessage = 'Une erreur de connexion est survenue';
        }
      } finally {
        this.loading = false
      }
    }
  },
  mounted() {
    this.imageInterval = setInterval(() => {
      this.currentImageIndex = (this.currentImageIndex + 1) % this.showcaseImages.length;
    }, 5000);
  },
  beforeUnmount() {
    if (this.imageInterval) clearInterval(this.imageInterval);
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap');

* { box-sizing: border-box; margin: 0; padding: 0; }

.auth-page {
  display: flex;
  min-height: 100vh;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* ───── LEFT PANEL ───── */
.auth-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #ffffff;
  position: relative;
  overflow: hidden;
}

.auth-left::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -30%;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(0,102,255,0.04) 0%, transparent 70%);
  pointer-events: none;
}

.auth-left-content {
  width: 100%;
  max-width: 420px;
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Brand */
.brand-header {
  margin-bottom: 40px;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.brand-name {
  font-size: 1.5rem;
  font-weight: 800;
  color: #1565C0;
  letter-spacing: -0.5px;
}

.logo-img {
  height: 38px;
  width: auto;
  object-fit: contain;
}

.brand-tagline {
  font-size: 0.85rem;
  color: #94a3b8;
  font-weight: 400;
  margin-left: 42px;
}

/* Form Header */
.form-header {
  margin-bottom: 32px;
}

.form-title {
  font-size: 1.85rem;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.5px;
  margin-bottom: 8px;
}

.form-subtitle {
  font-size: 0.95rem;
  color: #64748b;
  font-weight: 400;
}

/* Alert */
.alert {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 10px;
  margin-bottom: 24px;
  font-size: 0.875rem;
  animation: shake 0.4s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.alert-error {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.alert-icon { font-size: 1.1rem; }
.alert-close {
  margin-left: auto;
  background: none;
  border: none;
  color: #dc2626;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0 4px;
}

/* Form */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #334155;
}

.input-wrapper {
  display: flex;
  align-items: center;
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  padding: 0 14px;
  background: #f8fafc;
  transition: all 0.25s ease;
  height: 48px;
}

.input-wrapper:focus-within {
  border-color: #1565C0;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(21,101,192,0.1);
}

.input-wrapper.input-error {
  border-color: #ef4444;
  background: #fef2f2;
}

.input-icon {
  color: #94a3b8;
  display: flex;
  align-items: center;
  margin-right: 10px;
  flex-shrink: 0;
}

.input-wrapper:focus-within .input-icon {
  color: #0066ff;
}

.form-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 0.925rem;
  font-family: inherit;
  color: #0f172a;
}

.form-input::placeholder {
  color: #94a3b8;
}

.toggle-password {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 4px;
  transition: color 0.2s;
}

.toggle-password:hover {
  color: #64748b;
}

.field-error {
  font-size: 0.78rem;
  color: #ef4444;
  font-weight: 500;
}

/* Submit Button */
.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #1565C0 0%, #0D47A1 100%);
  color: white;
  font-size: 0.95rem;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 4px;
  position: relative;
  overflow: hidden;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0; left: -100%;
  width: 100%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.15), transparent);
  transition: left 0.5s;
}

.submit-btn:hover::before {
  left: 100%;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #0D47A1 0%, #0a3475 100%);
  transform: translateY(-1px);
  box-shadow: 0 8px 25px rgba(21,101,192,0.35);
}

.submit-btn:active {
  transform: translateY(0);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2.5px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Footer */
.auth-footer {
  margin-top: 32px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.auth-footer p {
  font-size: 0.875rem;
  color: #64748b;
}

.auth-link {
  color: #0066ff;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.2s;
}

.auth-link:hover {
  color: #0D47A1;
  text-decoration: underline;
}

.back-link {
  font-size: 0.825rem;
  color: #94a3b8;
  text-decoration: none;
  transition: color 0.2s;
}

.back-link:hover {
  color: #64748b;
}

/* ───── RIGHT PANEL ───── */
.auth-right {
  flex: 1;
  position: relative;
  overflow: hidden;
  display: none;
}

.image-slideshow {
  position: absolute;
  inset: 0;
}

.showcase-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  inset: 0;
}

.image-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    rgba(0, 20, 60, 0.3) 0%,
    rgba(0, 20, 60, 0.15) 40%,
    rgba(0, 20, 60, 0.5) 80%,
    rgba(0, 20, 60, 0.85) 100%
  );
  z-index: 1;
}

.showcase-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 50px 40px;
  z-index: 2;
  color: white;
}

.showcase-badge {
  display: inline-block;
  padding: 6px 16px;
  background: rgba(21, 101, 192, 0.85);
  border-radius: 50px;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  margin-bottom: 16px;
  backdrop-filter: blur(10px);
}

.showcase-title {
  font-size: 1.75rem;
  font-weight: 700;
  margin-bottom: 8px;
  letter-spacing: -0.3px;
}

.showcase-desc {
  font-size: 0.95rem;
  color: rgba(255,255,255,0.8);
  font-weight: 400;
  margin-bottom: 24px;
  max-width: 400px;
}

.slideshow-dots {
  display: flex;
  gap: 8px;
}

.dot {
  width: 32px;
  height: 4px;
  border-radius: 2px;
  border: none;
  background: rgba(255,255,255,0.3);
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0;
}

.dot.active {
  background: white;
  width: 48px;
}

.dot:hover {
  background: rgba(255,255,255,0.6);
}

/* Slide Transition */
.slide-fade-enter-active {
  transition: all 0.6s ease-out;
}
.slide-fade-leave-active {
  transition: all 0.4s ease-in;
}
.slide-fade-enter-from {
  opacity: 0;
  transform: scale(1.05);
}
.slide-fade-leave-to {
  opacity: 0;
  transform: scale(0.98);
}

/* ───── RESPONSIVE ───── */
@media (min-width: 900px) {
  .auth-right {
    display: block;
  }
}

@media (max-width: 899px) {
  .auth-left {
    padding: 30px 20px;
  }
  .form-title {
    font-size: 1.5rem;
  }
}
</style>