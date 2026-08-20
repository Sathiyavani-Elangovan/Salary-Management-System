import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService, Notification } from '../../services/notification.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notification-container">
      <div *ngFor="let notification of notifications$ | async" 
           class="notification"
           [ngClass]="'notification-' + notification.type"
           (click)="remove(notification.id)">
        <div class="notification-icon">
          <span *ngIf="notification.type === 'success'">✓</span>
          <span *ngIf="notification.type === 'error'">✕</span>
          <span *ngIf="notification.type === 'info'">ⓘ</span>
          <span *ngIf="notification.type === 'warning'">⚠</span>
        </div>
        <div class="notification-message">{{ notification.message }}</div>
        <button class="notification-close" (click)="remove(notification.id)">×</button>
      </div>
    </div>
  `,
  styles: [`
    .notification-container {
      position: fixed;
      top: 80px;
      right: 20px;
      z-index: 10000;
      display: flex;
      flex-direction: column;
      gap: 12px;
      max-width: 400px;
    }

    .notification {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px 20px;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      cursor: pointer;
      animation: slideIn 0.3s ease-out;
      transition: all 0.3s ease;
      background: white;
      border-left: 4px solid;
    }

    .notification:hover {
      transform: translateX(-5px);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
    }

    @keyframes slideIn {
      from {
        transform: translateX(400px);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    .notification-icon {
      font-size: 24px;
      font-weight: bold;
      min-width: 28px;
      text-align: center;
    }

    .notification-message {
      flex: 1;
      font-size: 14px;
      line-height: 1.4;
      color: #2c3e50;
      font-weight: 500;
    }

    .notification-close {
      background: none;
      border: none;
      font-size: 24px;
      cursor: pointer;
      color: #95a5a6;
      padding: 0;
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: color 0.2s;
      line-height: 1;
    }

    .notification-close:hover {
      color: #2c3e50;
    }

    .notification-success {
      border-left-color: #27ae60;
    }

    .notification-success .notification-icon {
      color: #27ae60;
    }

    .notification-error {
      border-left-color: #e74c3c;
    }

    .notification-error .notification-icon {
      color: #e74c3c;
    }

    .notification-info {
      border-left-color: #3498db;
    }

    .notification-info .notification-icon {
      color: #3498db;
    }

    .notification-warning {
      border-left-color: #f39c12;
    }

    .notification-warning .notification-icon {
      color: #f39c12;
    }

    @media (max-width: 768px) {
      .notification-container {
        right: 10px;
        left: 10px;
        max-width: none;
      }
    }
  `]
})
export class NotificationComponent {
  notifications$ = this.notificationService.notifications$;

  constructor(private notificationService: NotificationService) {}

  remove(id: number): void {
    this.notificationService.remove(id);
  }
}
