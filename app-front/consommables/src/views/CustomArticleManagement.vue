<template>
  <div class="custom-articles-page">
    <v-container fluid>
      <div class="mb-4">
        <v-btn variant="text" prepend-icon="mdi-arrow-left" @click="$router.push('/admin/catalogue')">
          Retour au Catalogue
        </v-btn>
      </div>
      <ArticleTable
        :title="'Gestion : ' + categoryName"
        :singularTitle="categoryName"
        :items="articles"
        :headers="headers"
        :loading="loading"
        @add-item="addArticle"
        @update-item="updateArticle"
        @delete-item="deleteArticle"
      />
    </v-container>
    <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000">
      {{ snackbarMessage }}
    </v-snackbar>
  </div>
</template>

<script>
import ArticleTable from '@/components/ArticleTable.vue'
import ArticleService from '@/services/ArticleService'

export default {
  components: {
    ArticleTable
  },
  props: {
    categoryName: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      articles: [],
      loading: false,
      snackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success',
      headers: [
        { title: 'Code Barre', key: 'codeBarre' },
        { title: 'Nom', key: 'nom' },
        { title: 'Fabricant', key: 'fabricant' },
        { title: 'Prix', key: 'prix' },
        { title: 'Quantité', key: 'quantite' },
        { title: 'Unité', key: 'unite' },
        { title: 'Famille', key: 'famille' },
        { title: 'Fournisseur', key: 'fournisseur' },
        { title: 'Statut', key: 'statut' },
        { title: 'Lieu', key: 'lieu' },
        { title: 'QR Code', key: 'qrCode' },
        { title: 'Image', key: 'image' },
        { title: 'Actions', key: 'actions', sortable: false }
      ]
    }
  },
  watch: {
    categoryName: {
      immediate: true,
      handler() {
        this.fetchArticles()
      }
    }
  },
  methods: {
    async fetchArticles() {
      this.loading = true
      try {
        const response = await ArticleService.getArticlesByCustomCategory(this.categoryName)
        this.articles = response.data || []
      } catch (error) {
        console.error('Erreur lors du chargement des articles:', error)
        this.showNotification('Erreur lors du chargement des articles', 'error')
      } finally {
        this.loading = false
      }
    },
    async addArticle(formData) {
      try {
        const payload = {
          ...formData,
          type: 'CUSTOM',
          customCategoryName: this.categoryName
        }
        await ArticleService.createArticle(payload)
        this.showNotification('Article ajouté avec succès', 'success')
        this.fetchArticles()
      } catch (error) {
        console.error('Erreur lors de l\'ajout:', error)
        this.showNotification('Erreur lors de l\'ajout de l\'article', 'error')
      }
    },
    async updateArticle({ id, data }) {
      try {
        const payload = {
          ...data,
          type: 'CUSTOM',
          customCategoryName: this.categoryName
        }
        await ArticleService.updateArticle(id, payload)
        this.showNotification('Article mis à jour avec succès', 'success')
        this.fetchArticles()
      } catch (error) {
        console.error('Erreur lors de la mise à jour:', error)
        this.showNotification('Erreur lors de la mise à jour de l\'article', 'error')
      }
    },
    async deleteArticle(id) {
      try {
        await ArticleService.deleteArticle(id)
        this.showNotification('Article supprimé avec succès', 'success')
        this.fetchArticles()
      } catch (error) {
        console.error('Erreur lors de la suppression:', error)
        this.showNotification('Erreur lors de la suppression de l\'article', 'error')
      }
    },
    showNotification(message, color = 'success') {
      this.snackbarMessage = message
      this.snackbarColor = color
      this.snackbar = true
    }
  }
}
</script>

<style scoped>
.custom-articles-page {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f4ff 100%);
  min-height: 100vh;
  padding: 20px 0;
}
</style>
