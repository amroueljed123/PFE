<template>
  <div class="home">

    <!-- HEADER -->
    <header class="site-header">
      <div class="container header-content">
        <div class="logo"><img :src="getImageUrl('logo.jpg')" alt="ETAP" class="logo-img" /> <span>ETAP</span></div>

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

      <!-- PRODUCT CATALOG -->
      <section class="section">
        <div class="container">
          <div class="section-header">
            <h2>Nos Consommables</h2>
            <p>Découvrez notre gamme complète de produits et équipements</p>
          </div>

          <!-- Image-based product cards -->
          <div class="products-grid">
            <div 
              v-for="(product, index) in imageProducts" 
              :key="'img-' + index"
              class="product-card image-card"
            >
              <div class="product-image-wrapper">
                <img :src="getImageUrl(product.image)" :alt="product.name" class="product-image" />
                <div class="product-overlay">
                  <span class="product-badge">{{ product.category }}</span>
                </div>
              </div>
              <div class="product-info">
                <h3>{{ product.name }}</h3>
                <p>{{ product.description }}</p>
              </div>
            </div>
          </div>

          <!-- Icon-based product cards -->
          <div class="products-icon-grid">
            <div 
              v-for="(product, index) in iconProducts" 
              :key="'icon-' + index"
              class="product-card icon-card"
            >
              <div class="icon-circle">
                <i :class="'mdi ' + product.icon"></i>
              </div>
              <h3>{{ product.name }}</h3>
              <p>{{ product.description }}</p>
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
      <div class="container footer-grid">
        
        <!-- Brand & Quick Links -->
        <div class="footer-column brand-col">
          <div class="footer-brand">
            <img :src="getImageUrl('logo.jpg')" alt="ETAP" class="footer-logo-img" />
            <p>Solutions numériques pour l'industrie pétrolière.</p>
          </div>
          <div class="footer-links">
            <a href="#">Confidentialité</a>
            <a href="#">Conditions</a>
            <a href="#">Contact</a>
          </div>
        </div>

        <!-- Contact Infos -->
        <div class="footer-column contact-col">
          <h4>Nos Adresses</h4>
          
          <div class="contact-item">
            <strong>Siège</strong>
            <p>54, Avenue Mohamed V - 1002 Tunis, Tunisie</p>
            <p>Tel: (+216) 71 28 50 97</p>
          </div>

          <div class="contact-item">
            <strong>Kheireddine Pacha</strong>
            <p>27 bis, Av. Kheireddine Pacha, Tunis 1002</p>
            <p>Tel: (+216) 70 24 90 00</p>
          </div>

          <div class="contact-item">
            <strong>CRDP</strong>
            <p>4 Rue des entrepreneurs - 2035 Charguia II Ariana</p>
            <p>Tel: (+216) 70 83 84 40</p>
          </div>
        </div>

        <!-- Map -->
        <div class="footer-column map-col">
          <h4>Nous trouver (Siège)</h4>
          <div id="mapContainer" class="map-container"></div>
        </div>

      </div>

      <div class="container footer-bottom">
        <div class="copyright">
          © 2026 ETAP. Tous droits réservés.
        </div>
      </div>
    </footer>

  </div>
</template>

<script>
import "leaflet/dist/leaflet.css";
import L from "leaflet";

// Fix for default marker icon in Vite/Webpack
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

export default {
  name: "Home",
  data() {
    return {
      currentImageIndex: 0,
      imageInterval: null,
      map: null,

      heroImages: [
        "etap_hero_banner_1770414421217.png",
        "etap_hero_banner_1770414448785.png",
        "etap_hero_image_1770414474106.png"
      ],

      imageProducts: [
        {
          image: 'cartouches.jpg',
          name: 'Cartouches d\'Encre',
          category: 'Impression',
          description: 'Cartouches compatibles pour toutes marques d\'imprimantes jet d\'encre.'
        },
        {
          image: 'czrtouches1.jpg',
          name: 'Toners Laser',
          category: 'Impression',
          description: 'Toners haute capacité pour imprimantes laser professionnelles.'
        },
        {
          image: 'consommables.jpg',
          name: 'Équipements IT',
          category: 'Informatique',
          description: 'Écrans, ordinateurs, câbles et accessoires informatiques.'
        },
        {
          image: 'depot.jpg',
          name: 'Gestion de Stock',
          category: 'Logistique',
          description: 'Solutions de stockage et logistique pour vos consommables.'
        }
      ],

      iconProducts: [
        {
          icon: 'mdi-printer',
          name: 'Papier A4 / A3',
          description: 'Ramettes de papier haute qualité pour impression professionnelle.'
        },
        {
          icon: 'mdi-usb-flash-drive',
          name: 'Clés USB & Stockage',
          description: 'Supports de stockage portables et disques durs externes.'
        },
        {
          icon: 'mdi-cable-data',
          name: 'Câbles & Connectique',
          description: 'Câbles réseau, USB, HDMI et adaptateurs professionnels.'
        },
        {
          icon: 'mdi-tools',
          name: 'Fournitures de Bureau',
          description: 'Stylos, classeurs, agrafeuses et accessoires de bureau.'
        }
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
    },
    initMap() {
      // Siège coordinates (54 Ave Mohamed V, Tunis) - Approx
      const lat = 36.8123;
      const lng = 10.1658; 

      this.map = L.map('mapContainer').setView([lat, lng], 15);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
      }).addTo(this.map);

      L.marker([lat, lng]).addTo(this.map)
        .bindPopup('<b>ETAP Siège</b><br>54, Avenue Mohamed V')
        .openPopup();
    }
  },


  mounted() {
    this.rotateImages();
    this.initMap();
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
  color: #1565C0;
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
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  font-size: 1.5rem;
  color: #1565C0;
  text-decoration: none;
}

.logo-img {
  height: 38px;
  width: auto;
  object-fit: contain;
}

.nav a {
  margin: 0 15px;
  text-decoration: none;
  color: #374151;
  font-weight: 500;
}

.nav a:hover {
  color: #1565C0;
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
  background: rgba(13, 71, 161, 0.65);
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
  background: #1565C0;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}

.btn-primary:hover {
  background: #0D47A1;
}

.btn-outline {
  border: 2px solid #1565C0;
  background: transparent;
  color: #1565C0;
  padding: 10px 22px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}

.btn-outline:hover {
  background: #1565C0;
  color: white;
}

.large {
  padding: 14px 30px;
  font-size: 1rem;
}

/* ===== PRODUCT CATALOG ===== */

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.product-card {
  background: white;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
  transition: all 0.35s ease;
  cursor: default;
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(21, 101, 192, 0.15);
  border-color: #bbdefb;
}

.product-image-wrapper {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: #f0f4f8;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.product-card:hover .product-image {
  transform: scale(1.08);
}

.product-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 50%, rgba(13, 71, 161, 0.7) 100%);
  display: flex;
  align-items: flex-end;
  padding: 14px;
  opacity: 0;
  transition: opacity 0.35s ease;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.product-badge {
  display: inline-block;
  padding: 4px 14px;
  background: rgba(255, 255, 255, 0.92);
  color: #0D47A1;
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.8px;
  text-transform: uppercase;
  border-radius: 50px;
}

.product-info {
  padding: 18px 20px 22px;
}

.product-info h3 {
  font-size: 1rem;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 6px;
}

.product-info p {
  font-size: 0.82rem;
  color: #64748b;
  line-height: 1.5;
  margin: 0;
}

/* Icon product cards */
.products-icon-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.icon-card {
  text-align: center;
  padding: 36px 24px 30px;
}

.icon-circle {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 18px;
  transition: all 0.35s ease;
}

.icon-card:hover .icon-circle {
  background: linear-gradient(135deg, #1565C0 0%, #0D47A1 100%);
  transform: scale(1.1);
}

.icon-circle i {
  font-size: 1.6rem;
  color: #1565C0;
  transition: color 0.35s;
}

.icon-card:hover .icon-circle i {
  color: white;
}

.icon-card h3 {
  font-size: 0.95rem;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 6px;
}

.icon-card p {
  font-size: 0.8rem;
  color: #64748b;
  line-height: 1.5;
  margin: 0;
}

@media (max-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .products-icon-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
  .products-icon-grid {
    grid-template-columns: 1fr;
  }
}

/* ===== CTA ===== */

.cta-section {
  background: #0D47A1;
  color: white;
  padding: 100px 0;
  text-align: center;
}

.cta-section h2 {
  margin-bottom: 20px;
}

/* ===== FOOTER ===== */

.site-footer {
  background: #0a1929;
  color: #cbd5e1;
  padding: 60px 0 30px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
}

.footer-column h4 {
  color: white;
  margin-bottom: 20px;
  font-size: 1.1rem;
  border-bottom: 2px solid #1565C0;
  display: inline-block;
  padding-bottom: 5px;
}

.footer-brand {
  margin-bottom: 20px;
}

.contact-item {
  margin-bottom: 20px;
  font-size: 0.9rem;
}

.contact-item strong {
  display: block;
  color: white;
  margin-bottom: 4px;
}

.contact-item p {
  margin: 0;
  line-height: 1.4;
}

.footer-links {
  display: flex;
  gap: 15px;
}

.footer-links a {
  color: #cbd5e1;
  text-decoration: none;
  font-size: 0.9rem;
}

.footer-links a:hover {
  color: white;
  text-decoration: underline;
}

.map-container {
  height: 250px;
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  z-index: 1;
}

.footer-bottom {
  text-align: center;
  padding-top: 30px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 0.85rem;
  color: #64748b;
}

@media (max-width: 900px) {
  .footer-grid {
    grid-template-columns: 1fr;
    gap: 30px;
  }
}

</style>