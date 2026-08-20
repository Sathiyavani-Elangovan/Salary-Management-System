import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check if user is authenticated
  if (!authService.isAuthenticated) {
    router.navigate(['/login']);
    return false;
  }

  // Verify token exists and is valid
  const user = authService.currentUserValue;
  if (!user || !user.token) {
    // Token missing, clear auth and redirect
    authService.logout();
    router.navigate(['/login']);
    return false;
  }

  // Optional: Check if token is expired (if you have expiry in token)
  try {
    // Decode JWT token to check expiry
    const tokenPayload = JSON.parse(atob(user.token.split('.')[1]));
    const expiryTime = tokenPayload.exp * 1000; // Convert to milliseconds
    
    if (Date.now() >= expiryTime) {
      // Token expired, logout and redirect
      authService.logout();
      router.navigate(['/login']);
      return false;
    }
  } catch (error) {
    // If token parsing fails, assume invalid and logout
    console.error('Invalid token format:', error);
    authService.logout();
    router.navigate(['/login']);
    return false;
  }

  return true;
};
