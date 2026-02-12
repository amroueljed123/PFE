<template>
  <div class="home">

    <!-- HEADER -->
    <header class="site-header">
      <div class="container header-content">
        <div class="logo">ETAP</div>

        <nav class="nav">
          <a href="#">Accueil</a>
          <a href="#">Fonctionnalités</a>
          <a href="#">Infrastructure</a>
          <a href="#">Contact</a>
        </nav>

        <div class="header-actions">
          <button class="btn-outline" @click="login">Se connecter</button>
          <button class="btn-primary" @click="creeCompte">Créer un compte</button>
        </div>
      </div>
    </header>

    <main>

      <!-- HERO -->
      <section 
        class="hero"
        :style="{ backgroundImage: `url(${getImageUrl(heroImages[currentImageIndex])})` }"
      >
        <div class="hero-overlay"></div>

        <div class="container hero-content">
          <div class="hero-text">
            <h1>
              Solutions numériques avancées <br />
              pour l'industrie pétrolière
            </h1>

            <p>
              Une plateforme sécurisée et performante conçue pour
              optimiser vos opérations, garantir la conformité
              et améliorer la productivité.
            </p>

            <div class="hero-buttons">
              <button class="btn-primary large" @click="creeCompte">
                Démarrer maintenant
              </button>
              <button class="btn-outline large" @click="login">
                Se connecter
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- FEATURES -->
      <section class="section">
        <div class="container">
          <div class="section-header">
            <h2>Nos Fonctionnalités</h2>
            <p>Des outils puissants pour une gestion optimale</p>
          </div>

          <div class="features-grid">
            <article 
              v-for="(feature, index) in features" 
              :key="index"
              class="feature-card"
            >
              <div class="feature-icon">
                <i :class="`mdi ${feature.icon}`"></i>
              </div>
              <h3>{{ feature.title }}</h3>
              <p>{{ feature.description }}</p>
            </article>
          </div>
        </div>
      </section>

      <!-- STATS -->
      <section class="section light">
        <div class="container">
          <div class="section-header">
            <h2>Nos Chiffres Clés</h2>
          </div>

          <div class="stats-grid">
            <div v-for="(stat, index) in stats" :key="index" class="stat-box">
              <h3>{{ stat.value }}</h3>
              <p>{{ stat.label }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA -->
      <section class="cta-section">
        <div class="container cta-content">
          <h2>Prêt à collaborer avec ETAP ?</h2>
          <p>Rejoignez notre plateforme dès aujourd'hui.</p>
          <button class="btn-primary large" @click="creeCompte">
            Créer un compte gratuitement
          </button>
        </div>
      </section>

    </main>

    <!-- FOOTER -->
    <footer class="site-footer">
      <div class="container footer-content">
        <div>
          <strong>ETAP</strong>
          <p>Solutions numériques pour l'industrie pétrolière.</p>
        </div>

        <div class="footer-links">
          <a href="#">Confidentialité</a>
          <a href="#">Conditions</a>
          <a href="#">Contact</a>
        </div>

        <div class="copyright">
          © 2026 ETAP. Tous droits réservés.
        </div>
      </div>
    </footer>

  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      currentImageIndex: 0,
      interval: null,

      heroImages: [
        "etap_hero_banner_1770414421217.png",
        "etap_hero_banner_1770414448785.png",
        "etap_hero_image_1770414474106.png"
      ],

      features: [
        {
          icon: "mdi-shield-check-outline",
          title: "Sécurité Avancée",
          description: "Protection complète et conformité internationale."
        },
        {
          icon: "mdi-speedometer",
          title: "Performance Optimale",
          description: "Infrastructure haute disponibilité 24/7."
        },
        {
          icon: "mdi-radar",
          title: "Monitoring Temps Réel",
          description: "Surveillance continue et alertes intelligentes."
        }
      ],

      stats: [
        { value: "100%", label: "Conformité" },
        { value: "24/7", label: "Disponibilité" },
        { value: "10 000+", label: "Utilisateurs" }
      ]
    };
  },

  methods: {
    login() {
      this.$router.push("/login");
    },
    creeCompte() {
      this.$router.push("/cree-compte");
    },
    rotateImages() {
      this.interval = setInterval(() => {
        this.currentImageIndex =
          (this.currentImageIndex + 1) % this.heroImages.length;
      }, 5000);
    },
    getImageUrl(name) {
      return new URL(`../assets/${name}`, import.meta.url).href;
    }
  },

  mounted() {
    this.rotateImages();
  },

  beforeUnmount() {
    clearInterval(this.interval);
  }
};
</script>

<style scoped>

/* ===== GLOBAL ===== */

.home {
  font-family: "Inter", sans-serif;
  color: #1f2937;
  background: #ffffff;
}

.container {
  width: 90%;
  max-width: 1200px;
  margin: auto;
}

.section {
  padding: 100px 0;
}

.section.light {
  background: #f5f9ff;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-header h2 {
  font-size: 2.5rem;
  color: #003366;
  margin-bottom: 10px;
}

.section-header p {
  color: #6b7280;
}

/* ===== HEADER ===== */

.site-header {
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
}

.logo {
  font-weight: 700;
  font-size: 1.5rem;
  color: #003366;
}

.nav a {
  margin: 0 15px;
  text-decoration: none;
  color: #374151;
  font-weight: 500;
}

.nav a:hover {
  color: #0056b3;
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* ===== HERO ===== */

.hero {
  position: relative;
  height: 85vh;
  display: flex;
  align-items: center;
  background-size: cover;
  background-position: center;
  transition: background-image 1s ease-in-out;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 40, 80, 0.65);
}

.hero-content {
  position: relative;
  z-index: 2;
  color: white;
}

.hero-text {
  max-width: 650px;
}

.hero h1 {
  font-size: 3rem;
  line-height: 1.2;
  margin-bottom: 20px;
}

.hero p {
  margin-bottom: 30px;
  font-size: 1.1rem;
  color: #e5e7eb;
}

.hero-buttons {
  display: flex;
  gap: 15px;
}

/* ===== BUTTONS ===== */

.btn-primary {
  background: #0056b3;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}

.btn-primary:hover {
  background: #003f88;
}

.btn-outline {
  border: 2px solid #0056b3;
  background: transparent;
  color: #0056b3;
  padding: 10px 22px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}

.btn-outline:hover {
  background: #0056b3;
  color: white;
}

.large {
  padding: 14px 30px;
  font-size: 1rem;
}

/* ===== FEATURES ===== */

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.feature-card {
  background: white;
  padding: 40px;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  text-align: center;
  transition: 0.3s;
}

.feature-card:hover {
  box-shadow: 0 10px 30px rgba(0, 86, 179, 0.1);
}

.feature-icon {
  font-size: 2rem;
  color: #0056b3;
  margin-bottom: 20px;
}

/* ===== STATS ===== */

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 40px;
  text-align: center;
}

.stat-box h3 {
  font-size: 2.5rem;
  color: #0056b3;
}

.stat-box p {
  color: #6b7280;
}

/* ===== CTA ===== */

.cta-section {
  background: #003366;
  color: white;
  padding: 100px 0;
  text-align: center;
}

.cta-section h2 {
  margin-bottom: 20px;
}

/* ===== FOOTER ===== */

.site-footer {
  background: #0f172a;
  color: #cbd5e1;
  padding: 60px 0;
}

.footer-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.footer-links a {
  color: #cbd5e1;
  margin-right: 15px;
  text-decoration: none;
}

.footer-links a:hover {
  color: white;
}

</style>
