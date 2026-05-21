/**
 * auth.js – Frontend Helper Library
 * ===================================
 * Manages authentication, token storage, authorization headers,
 * and role-based access control for the Hotel Booking API.
 *
 * Usage:
 *   <script src="auth.js"></script>
 *   <script>
 *     // Login
 *     const data = await auth.login('username', 'password');
 *
 *     // Make authenticated API calls
 *     const res = await auth.apiFetch('/hotel/room-types');
 *
 *     // Guard a page
 *     auth.checkAccess(['ADMIN']);
 *   </script>
 *
 * API Base URL: http://localhost:8080/api
 *
 * Quick Reference – All Endpoints:
 * -----------------------------------
 * AUTH:
 *   POST   /auth/register          – Register new customer account
 *   POST   /auth/login             – Login (returns accessToken, refreshToken, user)
 *   POST   /auth/google            – Login with Google
 *   POST   /auth/forgot-password   – Send OTP to email
 *   POST   /auth/reset-password    – Reset password using OTP
 *   POST   /auth/logout            – Invalidate token
 *
 * GUEST (public - no token needed):
 *   GET    /hotel                            – Hotel info
 *   GET    /hotel/room-types                 – Paginated active room types
 *   GET    /hotel/room-types/search          – Search/filter room types
 *   GET    /hotel/room-types/{id}            – Room type detail with images, items, reviews
 *   GET    /hotel/room-types/availability    – Check room availability
 *
 * CUSTOMER (requires ROLE_CUSTOMER):
 *   GET    /customer/me                            – View profile
 *   PUT    /customer/me                            – Update profile
 *   PUT    /customer/me/avatar                     – Update avatar (multipart)
 *   PUT    /customer/me/change-password            – Change password
 *   POST   /customer/bookings                      – Create new booking → 201
 *   GET    /customer/bookings                      – Booking history (?status=CONFIRMED)
 *   GET    /customer/bookings/{id}                 – Booking detail
 *   PUT    /customer/bookings/{id}/cancel          – Cancel booking
 *   PUT    /customer/bookings/{id}/refund          – Request refund
 *   POST   /customer/bookings/payment/vnpay        – Create VNPay payment URL
 *   GET    /customer/bookings/payment/vnpay-return – VNPay callback
 *   POST   /customer/reviews                       – Submit review → 201
 *   GET    /customer/notifications                 – Notifications (?page=0&size=10)
 *
 * ADMIN (requires ROLE_ADMIN):
 *   GET    /admin/hotel                           – Hotel info
 *   PUT    /admin/hotel                           – Update hotel
 *   POST   /admin/hotel/images                    – Upload hotel images
 *   PUT    /admin/hotel/images/{id}               – Update image
 *   DELETE /admin/hotel/images/{id}               – Delete image
 *   POST   /admin/hotel/amenities                 – Add amenities → 201
 *   PUT    /admin/hotel/amenities/{id}            – Update amenity
 *   DELETE /admin/hotel/amenities/{id}            – Delete amenity
 *
 *   GET    /admin/room-types                      – Paginated room types (?status=1)
 *   GET    /admin/room-types/{id}                 – Room type detail
 *   POST   /admin/room-types                      – Create room type → 201
 *   PUT    /admin/room-types/{id}                 – Update room type
 *   DELETE /admin/room-types/{id}                 – Delete room type
 *   GET    /admin/room-types/{id}/images          – Room type images
 *   POST   /admin/room-types/{id}/images          – Upload images → 201
 *   PUT    /admin/room-types/{id}/images/{imgId}  – Update image
 *   DELETE /admin/room-types/{id}/images/{imgId}  – Delete image
 *   GET    /admin/room-types/{id}/items           – Items in room type
 *   POST   /admin/room-types/{id}/items           – Add items → 201
 *   DELETE /admin/room-types/{id}/items/{itemId}  – Remove item
 *   GET    /admin/room-types/base-items           – All base items (paginated)
 *   POST   /admin/room-types/base-items           – Create base items → 201
 *   PUT    /admin/room-types/base-items/{id}      – Update base item
 *   DELETE /admin/room-types/base-items/{id}      – Delete base item
 *   POST   /admin/room-types/base-items/{id}/image – Upload item image → 201
 *
 *   GET    /admin/rooms                           – All rooms (?roomTypeId=1)
 *   GET    /admin/rooms/{id}                      – Room detail
 *   POST   /admin/rooms                           – Create room → 201
 *   PUT    /admin/rooms/{id}                      – Update room
 *   DELETE /admin/rooms/{id}                      – Delete room
 *
 *   GET    /admin/bookings                        – All bookings (paginated)
 *   GET    /admin/bookings/{id}                   – Booking detail
 *   GET    /admin/bookings/search                 – Search bookings
 *   GET    /admin/bookings/filter                 – Filter bookings
 *   PUT    /admin/bookings/{id}/cancel            – Cancel booking
 *
 *   GET    /admin/reviews                         – All reviews (?replyStatus=REPLIED)
 *   GET    /admin/reviews/{id}                    – Review detail
 *   GET    /admin/reviews/search                  – Search reviews
 *   PUT    /admin/reviews/{id}/reply              – Reply to review
 *   DELETE /admin/reviews/{id}                    – Delete review
 *
 *   GET    /admin/staff                           – Staff list
 *   GET    /admin/staff/{id}                      – Staff detail
 *   POST   /admin/staff                           – Create staff → 201
 *   PUT    /admin/staff/{id}                      – Update staff profile
 *
 *   GET    /admin/dashboard/booking-statistic     – Booking stats
 *   GET    /admin/dashboard/review-rate-statistic – Review stats
 *   GET    /admin/dashboard/revenue-statistic     – Revenue stats
 *   GET    /admin/dashboard/payment-statistic     – Payment stats
 *   GET    /admin/dashboard/ota-statistic         – OTA stats
 *   GET    /admin/dashboard/staff-statistic       – Staff stats
 *   GET    /admin/dashboard/room-type-statistic   – Room type booking stats
 *
 *   GET    /admin/salary                          – Salary sheets
 *   POST   /admin/salary/calculate-all            – Calculate salary
 *   PUT    /admin/salary/{id}/paid                – Mark salary paid
 *
 *   GET    /admin/shifts                          – All shifts
 *   POST   /admin/shifts                          – Create shift → 201
 *   PUT    /admin/shifts/{id}                     – Update shift
 *
 *   GET    /admin/attendance                      – Attendance list
 *
 *   GET    /admin/ota-channels                    – OTA channels
 *   POST   /admin/ota-channels                    – Create OTA → 201
 *   PUT    /admin/ota-channels/{id}               – Update OTA
 *   POST   /admin/ota-channels/booking            – OTA booking webhook → 201
 *
 * RECEPTIONIST (requires ROLE_RECEPTIONIST):
 *   POST   /receptionist/bookings/check           – Check availability
 *   POST   /receptionist/bookings/walk-in         – Walk-in booking → 201
 *   GET    /receptionist/bookings                 – All bookings (paginated)
 *   GET    /receptionist/bookings/{id}            – Booking detail
 *   GET    /receptionist/bookings/search          – Search bookings
 *   PUT    /receptionist/bookings/{id}/cancel     – Cancel
 *   PUT    /receptionist/bookings/{id}/refund     – Refund
 *   POST   /receptionist/bookings/pay             – Cash/bank payment
 *   POST   /receptionist/bookings/{id}/check-in  – Check-in
 *   POST   /receptionist/bookings/{id}/check-out – Check-out
 *   GET    /receptionist/bookings/upcoming-checkin  – Upcoming check-ins
 *   GET    /receptionist/bookings/upcoming-checkout – Upcoming check-outs
 *
 * STAFF (ROLE_CLEANER or ROLE_RECEPTIONIST):
 *   GET    /staff/me                   – View profile
 *   GET    /staff/my-current-week      – Current week shifts
 *   POST   /staff/check-in             – Check in
 *   POST   /staff/check-out/{id}       – Check out
 *   GET    /staff/my-attendance        – Attendance history
 *   GET    /staff/salary/current-month – Current month salary
 *
 * CLEANER (requires ROLE_CLEANER):
 *   GET    /cleaner/rooms              – Assigned cleaning tasks
 *   PUT    /cleaner/rooms/{id}/status  – Update room cleaning status
 */

const API_BASE_URL = 'http://localhost:8080/api';

const auth = {
    /**
     * Login and store tokens + user profile in localStorage
     * @param {string} username
     * @param {string} password
     * @returns {Promise<object>} Unwrapped data: { accessToken, refreshToken, user }
     */
    async login(username, password) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            if (!response.ok) {
                let errorMessage = 'Login failed';
                try {
                    const errData = await response.json();
                    errorMessage = errData.message || errorMessage;
                } catch (e) {
                    errorMessage = await response.text() || errorMessage;
                }
                throw new Error(errorMessage);
            }

            const responseData = await response.json(); // ApiResponse<LoginResponse>
            const data = responseData.data;             // { accessToken, refreshToken, user }

            if (!data) {
                throw new Error(responseData.message || 'No data received from server');
            }

            // Persist to localStorage for session across page navigations
            localStorage.setItem('jwt_token', data.accessToken);
            localStorage.setItem('refresh_token', data.refreshToken);
            localStorage.setItem('user_profile', JSON.stringify(data.user));

            return data;
        } catch (error) {
            console.error('[auth.login] Error:', error);
            throw error;
        }
    },

    /**
     * Logout – invalidate token on server and clear localStorage
     */
    async logout() {
        const token = this.getToken();
        if (token) {
            try {
                await fetch(`${API_BASE_URL}/auth/logout`, {
                    method: 'POST',
                    headers: this.getAuthHeader()
                });
            } catch (error) {
                console.error('[auth.logout] API call failed, clearing local storage anyway:', error);
            }
        }
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('refresh_token');
        localStorage.removeItem('user_profile');
        window.location.href = 'login.html';
    },

    /**
     * Get stored JWT access token
     * @returns {string|null}
     */
    getToken() {
        return localStorage.getItem('jwt_token');
    },

    /**
     * Get stored refresh token
     * @returns {string|null}
     */
    getRefreshToken() {
        return localStorage.getItem('refresh_token');
    },

    /**
     * Get stored user profile object
     * @returns {object|null} { id, username, email, fullName, phone, role, avatarUrl, ... }
     */
    getUser() {
        const userStr = localStorage.getItem('user_profile');
        return userStr ? JSON.parse(userStr) : null;
    },

    /**
     * Check if the user is authenticated (has a token)
     * @returns {boolean}
     */
    isAuthenticated() {
        return !!this.getToken();
    },

    /**
     * Get Authorization header object to include in fetch() calls
     * @returns {object} { Authorization: 'Bearer <token>' } or {}
     */
    getAuthHeader() {
        const token = this.getToken();
        return token ? { 'Authorization': `Bearer ${token}` } : {};
    },

    /**
     * Role-based access guard for page protection.
     * Call at the top of any protected page to verify role.
     * @param {string[]} allowedRoles e.g. ['ADMIN'] or ['ADMIN', 'RECEPTIONIST']
     * @returns {boolean} true if access granted, false if redirected
     */
    checkAccess(allowedRoles) {
        if (!this.isAuthenticated()) {
            alert('Please login to access this page!');
            window.location.href = 'login.html';
            return false;
        }

        const user = this.getUser();
        if (!user || !allowedRoles.includes(user.role)) {
            alert(`Access denied! This page is only for: ${allowedRoles.join(', ')}`);

            // Redirect based on role
            const roleRedirects = {
                'ADMIN': 'admin-test.html',
                'RECEPTIONIST': 'receptionist-dashboard.html',
                'CLEANER': 'cleaner-dashboard.html',
                'CUSTOMER': 'customer-dashboard.html'
            };
            const redirect = user && roleRedirects[user.role] ? roleRedirects[user.role] : 'login.html';
            window.location.href = redirect;
            return false;
        }
        return true;
    },

    /**
     * Generic authenticated API fetch wrapper.
     * Automatically attaches Authorization header and parses ApiResponse<T>.
     *
     * @param {string} endpoint  - API path (e.g. '/hotel/room-types')
     * @param {object} options   - fetch options (method, body, headers, etc.)
     * @returns {Promise<*>}     - Resolved with responseData.data value
     *
     * Usage examples:
     *   // GET
     *   const roomTypes = await auth.apiFetch('/hotel/room-types?page=0&size=10');
     *
     *   // POST with JSON body
     *   const booking = await auth.apiFetch('/customer/bookings', {
     *     method: 'POST',
     *     body: JSON.stringify({ roomTypeId: 1, checkIn: '2026-06-01', checkOut: '2026-06-05' })
     *   });
     *
     *   // PUT
     *   await auth.apiFetch('/customer/me', {
     *     method: 'PUT',
     *     body: JSON.stringify({ fullName: 'Nguyen Van A' })
     *   });
     *
     *   // DELETE
     *   await auth.apiFetch('/admin/rooms/5', { method: 'DELETE' });
     *
     *   // PATCH
     *   await auth.apiFetch('/admin/rooms/5', {
     *     method: 'PATCH',
     *     body: JSON.stringify({ status: 'CLEANING' })
     *   });
     */
    async apiFetch(endpoint, options = {}) {
        const method = options.method || 'GET';
        const hasBody = ['POST', 'PUT', 'PATCH'].includes(method);

        const headers = {
            ...this.getAuthHeader(),
            'Accept': 'application/json',
            ...(hasBody && !(options.body instanceof FormData)
                ? { 'Content-Type': 'application/json' }
                : {}),
            ...(options.headers || {})
        };

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            ...options,
            method,
            headers
        });

        const responseData = await response.json();

        if (!response.ok) {
            // Server returned error with ApiResponse format
            const error = new Error(responseData.message || `HTTP ${response.status}`);
            error.status = response.status;
            error.errors = responseData.errors || null;
            throw error;
        }

        return responseData.data;
    },

    /**
     * Build a paginated URL with common query params
     * @param {string} base   - base endpoint e.g. '/hotel/room-types'
     * @param {object} params - query params e.g. { page: 0, size: 10, keyword: 'deluxe' }
     * @returns {string} URL with query string
     */
    buildUrl(base, params = {}) {
        const filtered = Object.fromEntries(
            Object.entries(params).filter(([, v]) => v !== null && v !== undefined && v !== '')
        );
        const query = new URLSearchParams(filtered).toString();
        return query ? `${base}?${query}` : base;
    }
};

// Make auth available globally
window.auth = auth;
