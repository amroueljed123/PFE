import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'

const vuetify = createVuetify({
    components,
    directives,
    theme: {
        defaultTheme: 'myCustomTheme',
        themes: {
            myCustomTheme: {
                dark: false,
                colors: {
                    background: '#FFFFFF',
                    surface: '#FFFFFF',
                    primary: '#1565C0', // Professional Blue
                    secondary: '#424242',
                    error: '#B00020',
                    info: '#2196F3',
                    success: '#4CAF50',
                    warning: '#FB8C00',
                },
            },
        },
    },
})

createApp(App).use(vuetify).use(router).mount('#app')
