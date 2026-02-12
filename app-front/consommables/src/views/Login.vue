<template>
  <div class="login-page">
    <div class="login-overlay"></div>
    <v-container fluid class="login-container">
      <v-row align="center" justify="center" class="fill-height">
        <v-col cols="12" sm="10" md="6" lg="5" xl="4">
          <v-card class="login-card rounded-lg pa-2" elevation="8">
            <v-toolbar color="primary" dark flat>
              <v-toolbar-title class="text-h6 font-weight-bold">
                <v-icon start class="mr-2">mdi-account-circle</v-icon>
                Se connecter
              </v-toolbar-title>
            </v-toolbar>
            
            <v-card-text class="pa-6">
              <div class="text-subtitle-1 mb-4 text-medium-emphasis text-center">
                Connectez-vous à votre compte
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
              
              <v-form ref="loginForm" v-model="formValid" @submit.prevent="handleLogin">
                <v-text-field
                  v-model="email"
                  label="Email"
                  name="email"
                  prepend-inner-icon="mdi-email"
                  type="email"
                  variant="outlined"
                  color="primary"
                  class="mb-2"
                  :rules="emailRules"
                  :error-messages="emailErrors"
                  required
                ></v-text-field>

                <v-text-field
                  v-model="password"
                  id="password"
                  label="Mot de passe"
                  name="password"
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
              </v-form>
            </v-card-text>

            <v-card-actions class="pa-4 bg-transparent border-t">
              <v-btn text color="primary" class="enterprise-btn" @click="$router.push('/')">
                Retour
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn 
                color="primary" 
                class="px-6 enterprise-btn" 
                elevation="2" 
                @click="handleLogin"
                :loading="loading"
                :disabled="loading"
              >
                Se connecter
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
      email: '',
      password: '',
      formValid: false,
      showPassword: false,
      loading: false,
      errorMessage: '',
      emailErrors: [],
      passwordErrors: []
    }
  },
  computed: {
    emailRules() {
      return [
        v => !!v || 'L\'email est requis',
        v => /.+@.+\..+/.test(v) || 'L\'email doit être valide'
      ]
    },
    passwordRules() {
      return [
        v => !!v || 'Le mot de passe est requis',
        v => (v && v.length >= 6) || 'Le mot de passe doit contenir au moins 6 caractères'
      ]
    }
  },
  methods: {
    async handleLogin() {
      // Reset errors
      this.errorMessage = ''
      this.emailErrors = []
      this.passwordErrors = []

      // Validate form
      const { valid } = await this.$refs.loginForm.validate()
      
      if (!valid) {
        this.errorMessage = 'Veuillez corriger les erreurs dans le formulaire'
        return
      }

      // Additional custom validation
      if (!this.validateEmail(this.email)) {
        this.emailErrors = ['Format d\'email invalide']
        this.errorMessage = 'Veuillez vérifier votre email'
        return
      }

      if (this.password.length < 6) {
        this.passwordErrors = ['Le mot de passe est trop court']
        this.errorMessage = 'Le mot de passe doit contenir au moins 6 caractères'
        return
      }

      // Start loading
      this.loading = true

      try {
        const response = await UserService.login(this.email, this.password);
        
        if (response.success) {
          const user = response.user;
          // Store JWT token and user details
          if (response.token) {
            localStorage.setItem('jwt_token', response.token);
          }
          localStorage.setItem('user', JSON.stringify(user));
          localStorage.setItem('isAuthenticated', 'true');
          
          // Redirect based on user role
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
    },

    validateEmail(email) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      return emailRegex.test(email)
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  position: relative;
  background-image: url('@/assets/etap_hero_banner_1770414421217.png');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

.login-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(21, 101, 192, 0.9) 0%, rgba(13, 71, 161, 0.85) 100%);
  backdrop-filter: blur(3px);
}

.login-container {
  position: relative;
  z-index: 2;
  min-height: 100vh;
}

.login-card {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
}

.enterprise-btn {
  text-transform: none;
  font-weight: 600;
  letter-spacing: 0.5px;
}
</style>