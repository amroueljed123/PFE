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
          <h1 class="form-title">Créer un compte ✨</h1>
          <p class="form-subtitle">Rejoignez la plateforme de gestion ETAP</p>
        </div>

        <!-- Error Alert -->
        <div v-if="errorMessage" class="alert alert-error">
          <span class="alert-icon">⚠</span>
          <span>{{ errorMessage }}</span>
          <button class="alert-close" @click="errorMessage = ''">×</button>
        </div>

        <!-- Success Alert -->
        <div v-if="successMessage" class="alert alert-success">
          <span class="alert-icon">✓</span>
          <span>{{ successMessage }}</span>
          <button class="alert-close" @click="successMessage = ''">×</button>
        </div>

        <!-- Signup Form -->
        <form @submit.prevent="handleRegister" class="auth-form">
          <!-- Full Name -->
          <div class="form-group">
            <label class="form-label">Nom et Prénom</label>
            <div class="input-wrapper" :class="{ 'input-error': nameErrors.length }">
              <span class="input-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
              </span>
              <input
                v-model="fullName"
                type="text"
                placeholder="Jean Dupont"
                class="form-input"
                required
              />
            </div>
            <span v-if="nameErrors.length" class="field-error">{{ nameErrors[0] }}</span>
          </div>

          <!-- Email -->
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

          <!-- Password Row -->
          <div class="form-row">
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
                  <svg v-if="!showPassword" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                  </svg>
                  <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                    <line x1="1" y1="1" x2="23" y2="23"/>
                  </svg>
                </button>
              </div>
              <span v-if="passwordErrors.length" class="field-error">{{ passwordErrors[0] }}</span>
            </div>

            <div class="form-group">
              <label class="form-label">Confirmer</label>
              <div class="input-wrapper" :class="{ 'input-error': confirmPasswordErrors.length }">
                <span class="input-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                  </svg>
                </span>
                <input
                  v-model="confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  placeholder="••••••••"
                  class="form-input"
                  required
                />
                <button type="button" class="toggle-password" @click="showConfirmPassword = !showConfirmPassword">
                  <svg v-if="!showConfirmPassword" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                  </svg>
                  <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
                    <line x1="1" y1="1" x2="23" y2="23"/>
                  </svg>
                </button>
              </div>
              <span v-if="confirmPasswordErrors.length" class="field-error">{{ confirmPasswordErrors[0] }}</span>
            </div>
          </div>

          <!-- Password Strength -->
          <div v-if="password" class="password-strength-section">
            <div class="strength-bar-track">
              <div class="strength-bar-fill" :style="{ width: passwordStrength.percentage + '%', background: passwordStrength.color }"></div>
            </div>
            <span class="strength-label" :style="{ color: passwordStrength.color }">{{ passwordStrength.label }}</span>
          </div>

          <!-- Password Requirements -->
          <div v-if="password" class="requirements-grid">
            <div class="req-item" :class="{ met: passwordRequirements.minLength }">
              <span class="req-dot"></span>
              8+ caractères
            </div>
            <div class="req-item" :class="{ met: passwordRequirements.hasUppercase }">
              <span class="req-dot"></span>
              Majuscule
            </div>
            <div class="req-item" :class="{ met: passwordRequirements.hasLowercase }">
              <span class="req-dot"></span>
              Minuscule
            </div>
            <div class="req-item" :class="{ met: passwordRequirements.hasNumber }">
              <span class="req-dot"></span>
              Chiffre
            </div>
            <div class="req-item" :class="{ met: passwordRequirements.hasSpecial }">
              <span class="req-dot"></span>
              Caractère spécial
            </div>
          </div>

          <button type="submit" class="submit-btn" :disabled="loading">
            <span v-if="loading" class="spinner"></span>
            <span v-else>Créer mon Compte</span>
            <svg v-if="!loading" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="5" y1="12" x2="19" y2="12"/>
              <polyline points="12 5 19 12 12 19"/>
            </svg>
          </button>
        </form>

        <!-- Footer Links -->
        <div class="auth-footer">
          <p>Déjà un compte ? <router-link to="/login" class="auth-link">Se connecter</router-link></p>
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
      fullName: '',
      email: '',
      password: '',
      confirmPassword: '',
      showPassword: false,
      showConfirmPassword: false,
      loading: false,
      errorMessage: '',
      successMessage: '',
      nameErrors: [],
      emailErrors: [],
      passwordErrors: [],
      confirmPasswordErrors: [],
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
  computed: {
    passwordRequirements() {
      const p = this.password || ''
      return {
        minLength: p.length >= 8, 
        hasUppercase: /[A-Z]/.test(p),
        hasLowercase: /[a-z]/.test(p),
        hasNumber: /[0-9]/.test(p),
        hasSpecial: /[!@#$%^&*(),.?":{}|<>]/.test(p)
      }
    },
    passwordStrength() {
      if (!this.password) return { percentage: 0, label: '', color: '#94a3b8' }
      let s = 0
      const checks = [
        this.password.length >= 8,
        this.password.length >= 12,
        /[a-z]/.test(this.password),
        /[A-Z]/.test(this.password),
        /[0-9]/.test(this.password),
        /[!@#$%^&*(),.?":{}|<>]/.test(this.password)
      ]
      s = checks.filter(Boolean).length
      if (s <= 2) return { percentage: 33, label: 'Faible', color: '#ef4444' }
      if (s <= 4) return { percentage: 66, label: 'Moyen', color: '#f59e0b' }
      return { percentage: 100, label: 'Fort', color: '#22c55e' }
    }
  },
  methods: {
    getImageUrl(name) {
      return new URL(`../assets/${name}`, import.meta.url).href;
    },
    async handleRegister() {
      this.errorMessage = ''
      this.successMessage = ''
      this.nameErrors = []
      this.emailErrors = []
      this.passwordErrors = []
      this.confirmPasswordErrors = []

      // Validate
      if (!this.fullName || this.fullName.length < 3) {
        this.nameErrors = ['Le nom doit contenir au moins 3 caractères']
        return
      }
      if (!/^[a-zA-ZÀ-ÿ\s'-]+$/.test(this.fullName)) {
        this.nameErrors = ['Le nom ne peut contenir que des lettres']
        return
      }
      if (!this.email || !/.+@.+\..+/.test(this.email)) {
        this.emailErrors = ['Veuillez entrer un email valide']
        return
      }
      if (!this.password || this.password.length < 8) {
        this.passwordErrors = ['Le mot de passe doit contenir au moins 8 caractères']
        return
      }
      if (!/[A-Z]/.test(this.password)) {
        this.passwordErrors = ['Une lettre majuscule requise']
        return
      }
      if (!/[a-z]/.test(this.password)) {
        this.passwordErrors = ['Une lettre minuscule requise']
        return
      }
      if (!/[0-9]/.test(this.password)) {
        this.passwordErrors = ['Un chiffre requis']
        return
      }
      if (!/[!@#$%^&*(),.?":{}|<>]/.test(this.password)) {
        this.passwordErrors = ['Un caractère spécial requis']
        return
      }
      if (this.password !== this.confirmPassword) {
        this.confirmPasswordErrors = ['Les mots de passe ne correspondent pas']
        return
      }

      this.loading = true

      try {
        const user = {
          name: this.fullName,
          email: this.email,
          password: this.password,
          role: 'USER'
        };

        await UserService.createUser(user);
        this.successMessage = 'Compte créé avec succès! Redirection...'
        setTimeout(() => {
          this.$router.push('/login')
        }, 2000)
      } catch (error) {
        console.error('Registration error:', error)
        if (error.response && error.response.status === 409) {
          this.errorMessage = 'Cet email est déjà utilisé'
          this.emailErrors = ['Email déjà utilisé']
        } else {
          this.errorMessage = 'Une erreur est survenue lors de la création du compte'
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
  overflow-y: auto;
  overflow-x: hidden;
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
  max-width: 480px;
  padding: 20px 0;
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Brand */
.brand-header {
  margin-bottom: 28px;
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
  margin-bottom: 24px;
}

.form-title {
  font-size: 1.65rem;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.5px;
  margin-bottom: 6px;
}

.form-subtitle {
  font-size: 0.9rem;
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
  margin-bottom: 20px;
  font-size: 0.85rem;
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

.alert-success {
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #bbf7d0;
}

.alert-icon { font-size: 1.1rem; }
.alert-close {
  margin-left: auto;
  background: none;
  border: none;
  color: inherit;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0 4px;
}

/* Form */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-label {
  font-size: 0.8rem;
  font-weight: 600;
  color: #334155;
}

.input-wrapper {
  display: flex;
  align-items: center;
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  padding: 0 12px;
  background: #f8fafc;
  transition: all 0.25s ease;
  height: 44px;
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
  margin-right: 8px;
  flex-shrink: 0;
}

.input-wrapper:focus-within .input-icon {
  color: #1565C0;
}

.form-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 0.875rem;
  font-family: inherit;
  color: #0f172a;
  min-width: 0;
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
  font-size: 0.75rem;
  color: #ef4444;
  font-weight: 500;
}

/* Password Strength */
.password-strength-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.strength-bar-track {
  flex: 1;
  height: 4px;
  background: #e2e8f0;
  border-radius: 2px;
  overflow: hidden;
}

.strength-bar-fill {
  height: 100%;
  border-radius: 2px;
  transition: all 0.4s ease;
}

.strength-label {
  font-size: 0.72rem;
  font-weight: 600;
  white-space: nowrap;
}

/* Requirements */
.requirements-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 14px;
}

.req-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.72rem;
  color: #94a3b8;
  transition: color 0.2s;
}

.req-item.met {
  color: #22c55e;
}

.req-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #cbd5e1;
  transition: background 0.2s;
  flex-shrink: 0;
}

.req-item.met .req-dot {
  background: #22c55e;
}

/* Submit Button */
.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 13px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #1565C0 0%, #0D47A1 100%);
  color: white;
  font-size: 0.925rem;
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
  margin-top: 24px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.auth-footer p {
  font-size: 0.85rem;
  color: #64748b;
}

.auth-link {
  color: #1565C0;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.2s;
}

.auth-link:hover {
  color: #0D47A1;
  text-decoration: underline;
}

.back-link {
  font-size: 0.8rem;
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
    padding: 24px 16px;
  }
  .form-title {
    font-size: 1.4rem;
  }
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>