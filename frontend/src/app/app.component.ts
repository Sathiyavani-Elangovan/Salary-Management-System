import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { NotificationComponent } from './components/notification/notification.component';
import { NotificationService } from './services/notification.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, NotificationComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Salary Management System';
  menuOpen = false;

  constructor(
    public authService: AuthService,
    private notificationService: NotificationService
  ) {}

  get currentUser() {
    return this.authService.currentUserValue;
  }

  get isAuthenticated() {
    return this.authService.isAuthenticated;
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  logout(): void {
    this.notificationService.info('Logged out successfully. See you soon!');
    setTimeout(() => {
      this.authService.logout();
      this.menuOpen = false;
    }, 500);
  }
}
