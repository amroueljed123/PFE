import api from './api';

class UserService {
    getAllUsers() {
        return api.get('/users');
    }

    getUserById(id) {
        return api.get(`/users/${id}`);
    }

    createUser(user) {
        return api.post('/users', user);
    }

    updateUser(id, user) {
        return api.put(`/users/${id}`, user);
    }

    deleteUser(id) {
        return api.delete(`/users/${id}`);
    }

    getUserByEmail(email) {
        return api.get(`/users/email/${email}`);
    }

    async login(email, password) {
        try {
            const response = await api.post('/users/login', {
                email: email,
                password: password
            });
            
            if (response.data && response.data.success && response.data.user) {
                // Store JWT token
                if (response.data.token) {
                    localStorage.setItem('jwt_token', response.data.token);
                }
                // Store user info
                localStorage.setItem('user', JSON.stringify(response.data.user));
                localStorage.setItem('isAuthenticated', 'true');
                
                return { success: true, user: response.data.user, token: response.data.token };
            } else {
                return { success: false, message: response.data?.message || 'Email ou mot de passe incorrect' };
            }
        } catch (error) {
            console.error('Login error details:', error.response?.data);
            if (error.response && error.response.status === 401) {
                return { success: false, message: 'Email ou mot de passe incorrect' };
            } else if (error.response && error.response.status === 404) {
                return { success: false, message: 'Utilisateur non trouvé' };
            } else if (error.response?.data?.message) {
                return { success: false, message: error.response.data.message };
            }
            throw error;
        }
    }

    register(user) {
        return this.createUser(user);
    }
}

export default new UserService();
