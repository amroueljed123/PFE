<template>
  <div class="signup-page">
    <div class="signup-overlay"></div>
    <v-container fluid class="signup-container">
      <v-row align="center" justify="center" class="fill-height">
        <v-col cols="12" sm="10" md="6" lg="5" xl="4">
          <v-card class="signup-card rounded-lg pa-2" elevation="8">
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title class="text-h6 font-weight-bold">
                <v-icon start class="mr-2">mdi-account-plus</v-icon>
                Créer un compte
              </v-toolbar-title>
            </v-toolbar>
            
            <v-card-text class="pa-6">
              <div class="text-subtitle-1 mb-6 text-medium-emphasis text-center">
                Créez un compte pour accéder au portail
              </div>

              <!-- Error Alert -->
              <v-alert
                v-if="errorMessage"
                type="error"
                variant="tonal"
                closable
                class="mb-4"
                @click:close="errorMessage = ''"
              >
                {{ errorMessage }}
              </v-alert>

              <!-- Success Alert -->
              <v-alert
                v-if="successMessage"
                type="success"
                variant="tonal"
                closable
                class="mb-4"
                @click:close="successMessage = ''"
              >
                {{ successMessage }}
              </v-alert>

              <v-form ref="signupForm" v-model="formValid" @submit.prevent="handleRegister">
                <v-text-field
                  v-model="fullName"
                  label="Nom et Prénom"
                  prepend-inner-icon="mdi-account"
                  variant="outlined"
                  color="primary"
                  class="mb-2"
                  :rules="nameRules"
                  :error-messages="nameErrors"
                  required
                ></v-text-field>

                <v-text-field
                  v-model="email"
                  label="Email"
                  prepend-inner-icon="mdi-email"
                  type="email"
                  variant="outlined"
                  color="primary"
                  class="mb-2"
                  :rules="emailRules"
                  :error-messages="emailErrors"
                  required
                ></v-text-field>

                <v-row>
                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="password"
                      label="Mot de passe"
                      prepend-inner-icon="mdi-lock"
                      :type="showPassword ? 'text' : 'password'"
                      variant="outlined"
                      color="primary"
                      :rules="passwordRules"
                      :error-messages="passwordErrors"
                      :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                      @click:append-inner="showPassword = !showPassword"
                      required
                    ></v-text-field>
                    <!-- Password Strength Indicator -->
                    <div v-if="password" class="password-strength mb-2">
                      <div class="strength-bar">
                        <div 
                          class="strength-fill" 
                          :style="{ width: passwordStrength.percentage + '%', backgroundColor: passwordStrength.color }"
                        ></div>
                      </div>
                      <div class="strength-text" :style="{ color: passwordStrength.color }">
                        {{ passwordStrength.label }}
                      </div>
                    </div>
                  </v-col>
                  <v-col cols="12" md="6">
                     <v-text-field
                      v-model="confirmPassword"
                      label="Confirmer le mot de passe"
                      prepend-inner-icon="mdi-lock-check"
                      :type="showConfirmPassword ? 'text' : 'password'"
                      variant="outlined"
                      color="primary"
                      :rules="confirmPasswordRules"
                      :error-messages="confirmPasswordErrors"
                      :append-inner-icon="showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'"
                      @click:append-inner="showConfirmPassword = !showConfirmPassword"
                      required
                    ></v-text-field>
                  </v-col>
                </v-row>

                <!-- Password Requirements -->
                <v-card variant="outlined" class="pa-3 mb-3" v-if="password">
                  <div class="text-caption text-medium-emphasis mb-2">Exigences du mot de passe:</div>
                  <div class="requirement-item" :class="{ 'requirement-met': passwordRequirements.minLength }">
                    <v-icon size="small" :color="passwordRequirements.minLength ? 'success' : 'grey'">
                      {{ passwordRequirements.minLength ? 'mdi-check-circle' : 'mdi-circle-outline' }}
                    </v-icon>
                    <span class="ml-2">Au moins 8 caractères</span>
                  </div>
                  <div class="requirement-item" :class="{ 'requirement-met': passwordRequirements.hasUppercase }">
                    <v-icon size="small" :color="passwordRequirements.hasUppercase ? 'success' : 'grey'">
                      {{ passwordRequirements.hasUppercase ? 'mdi-check-circle' : 'mdi-circle-outline' }}
                    </v-icon>
                    <span class="ml-2">Une lettre majuscule</span>
                  </div>
                  <div class="requirement-item" :class="{ 'requirement-met': passwordRequirements.hasLowercase }">
                    <v-icon size="small" :color="passwordRequirements.hasLowercase ? 'success' : 'grey'">
                      {{ passwordRequirements.hasLowercase ? 'mdi-check-circle' : 'mdi-circle-outline' }}
                    </v-icon>
                    <span class="ml-2">Une lettre minuscule</span>
                  </div>
                  <div class="requirement-item" :class="{ 'requirement-met': passwordRequirements.hasNumber }">
                    <v-icon size="small" :color="passwordRequirements.hasNumber ? 'success' : 'grey'">
                      {{ passwordRequirements.hasNumber ? 'mdi-check-circle' : 'mdi-circle-outline' }}
                    </v-icon>
                    <span class="ml-2">Un chiffre</span>
                  </div>
                  <div class="requirement-item" :class="{ 'requirement-met': passwordRequirements.hasSpecial }">
                    <v-icon size="small" :color="passwordRequirements.hasSpecial ? 'success' : 'grey'">
                      {{ passwordRequirements.hasSpecial ? 'mdi-check-circle' : 'mdi-circle-outline' }}
                    </v-icon>
                    <span class="ml-2">Un caractère spécial</span>
                  </div>
                </v-card>
              </v-form>
            </v-card-text>

            <v-card-actions class="pa-4 bg-transparent border-t">
              <v-btn variant="text" color="primary" class="enterprise-btn" @click="$router.push('/login')">
                Déjà un compte?
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn 
                color="primary" 
                class="px-6 enterprise-btn" 
                elevation="2" 
                @click="handleRegister"
                :loading="loading"
                :disabled="loading"
              >
                Créer un compte
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
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
      formValid: false,
      showPassword: false,
      showConfirmPassword: false,
      loading: false,
      errorMessage: '',
      successMessage: '',
      nameErrors: [],
      emailErrors: [],
      passwordErrors: [],
      confirmPasswordErrors: [],
      nameRules: [
        v => !!v || 'Le nom est requis',
        v => (v && v.length >= 3) || 'Le nom doit contenir au moins 3 caractères',
        v => /^[a-zA-ZÀ-ÿ\s'-]+$/.test(v) || 'Le nom ne peut contenir que des lettres'
      ],
      emailRules: [
        v => !!v || 'L\'email est requis',
        v => /.+@.+\..+/.test(v) || 'L\'email doit être valide'
      ],
      passwordRules: [
        v => !!v || 'Le mot de passe est requis',
        v => (v && v.length >= 8) || 'Le mot de passe doit contenir au moins 8 caractères',
        v => /[A-Z]/.test(v) || 'Le mot de passe doit contenir au moins une lettre majuscule',
        v => /[a-z]/.test(v) || 'Le mot de passe doit contenir au moins une lettre minuscule',
        v => /[0-9]/.test(v) || 'Le mot de passe doit contenir au moins un chiffre',
        v => /[!@#$%^&*(),.?":{}|<>]/.test(v) || 'Le mot de passe doit contenir au moins un caractère spécial'
      ]
    }
  },
  computed: {
    confirmPasswordRules() {
      return [
        v => !!v || 'La confirmation du mot de passe est requise',
        v => v === this.password || 'Les mots de passe ne correspondent pas'
      ]
    },
    passwordRequirements() {
      const password = this.password || ''
      return {
        minLength: password.length >= 8,
        hasUppercase: /[A-Z]/.test(password),
        hasLowercase: /[a-z]/.test(password),
        hasNumber: /[0-9]/.test(password),
        hasSpecial: /[!@#$%^&*(),.?":{}|<>]/.test(password)
      }
    },
    passwordStrength() {
      if (!this.password) return { percentage: 0, label: '', color: 'grey' }

      
      let strength = 0
      const checks = [
        this.password.length >= 8,
        this.password.length >= 12,
        /[a-z]/.test(this.password),
        /[A-Z]/.test(this.password),
        /[0-9]/.test(this.password),
        /[!@#$%^&*(),.?":{}|<>]/.test(this.password)
      ]
      
      strength = checks.filter(Boolean).length
      
      if (strength <= 2) {
        return { percentage: 33, label: 'Faible', color: '#f44336' }
      } else if (strength <= 4) {
        return { percentage: 66, label: 'Moyen', color: '#ff9800' }
      } else {
        return { percentage: 100, label: 'Fort', color: '#4caf50' }
      }
    }
  },
  methods: {
    async handleRegister() {
      // Reset messages
      this.errorMessage = ''
      this.successMessage = ''
      this.nameErrors = []
      this.emailErrors = []
      this.passwordErrors = []
      this.confirmPasswordErrors = []

      // Validate form
      const { valid } = await this.$refs.signupForm.validate()
      
      if (!valid) {
        this.errorMessage = 'Veuillez corriger les erreurs dans le formulaire'
        return
      }

      // Additional custom validation... (keep existing checks)
      if (!this.validateName(this.fullName)) {
        this.nameErrors = ['Le nom doit contenir au moins 3 caractères et uniquement des lettres']
        this.errorMessage = 'Veuillez vérifier votre nom'
        return
      }

      if (!this.validateEmail(this.email)) {
        this.emailErrors = ['Format d\'email invalide']
        this.errorMessage = 'Veuillez vérifier votre email'
        return
      }

      if (!this.validatePassword(this.password)) {
        this.passwordErrors = ['Le mot de passe ne respecte pas les exigences']
        this.errorMessage = 'Le mot de passe doit respecter toutes les exigences'
        return
      }

      if (this.password !== this.confirmPassword) {
        this.confirmPasswordErrors = ['Les mots de passe ne correspondent pas']
        this.errorMessage = 'Les mots de passe ne correspondent pas'
        return
      }

      // Start loading
      this.loading = true

      try {
        const user = {
          name: this.fullName,
          email: this.email,
          password: this.password,
          role: 'USER' // Default role
        };

        await UserService.createUser(user);
        
        this.successMessage = 'Compte créé avec succès! Redirection vers la page de connexion...'
        
        // Redirect to login after success
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
    },

    validateName(name) {
      return name.length >= 3 && /^[a-zA-ZÀ-ÿ\s'-]+$/.test(name)
    },

    validateEmail(email) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      return emailRegex.test(email)
    },

    validatePassword(password) {
      return (
        password.length >= 8 &&
        /[A-Z]/.test(password) &&
        /[a-z]/.test(password) &&
        /[0-9]/.test(password) &&
        /[!@#$%^&*(),.?":{}|<>]/.test(password)
      )
    }
  }
}
</script>

<style scoped>
.signup-page {
  min-height: 100vh;
  position: relative;
  background-image: url('@/assets/etap_hero_image_1770414474106.png');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

.signup-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(21, 101, 192, 0.9) 0%, rgba(13, 71, 161, 0.85) 100%);
  backdrop-filter: blur(3px);
}

.signup-container {
  position: relative;
  z-index: 2;
  min-height: 100vh;
}

.signup-card {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
}

.enterprise-btn {
  text-transform: none;
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* Password Strength Styles */
.password-strength {
  margin-top: 8px;
}

.strength-bar {
  height: 4px;
  background-color: #e0e0e0;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 4px;
}

.strength-fill {
  height: 100%;
  transition: all 0.3s ease;
}

.strength-text {
  font-size: 0.75rem;
  font-weight: 500;
  text-align: right;
  transition: color 0.3s ease;
}

/* Password Requirement Styles */
.requirement-item {
  display: flex;
  align-items: center;
  font-size: 0.85rem;
  color: #757575;
  margin-bottom: 4px;
  transition: color 0.3s ease;
}

.requirement-item.requirement-met {
  color: #4caf50;
  font-weight: 500;
}
</style>