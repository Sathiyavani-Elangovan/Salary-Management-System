import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Notification {
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
  id: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  public notifications$: Observable<Notification[]> = this.notificationsSubject.asObservable();
  private nextId = 1;

  success(message: string): void {
    this.show(message, 'success');
  }

  error(message: string): void {
    this.show(message, 'error');
  }

  info(message: string): void {
    this.show(message, 'info');
  }

  warning(message: string): void {
    this.show(message, 'warning');
  }

  private show(message: string, type: 'success' | 'error' | 'info' | 'warning'): void {
    const notification: Notification = {
      message,
      type,
      id: this.nextId++
    };

    const currentNotifications = this.notificationsSubject.value;
    this.notificationsSubject.next([...currentNotifications, notification]);

    // Auto-remove after 4 seconds
    setTimeout(() => {
      this.remove(notification.id);
    }, 4000);
  }

  remove(id: number): void {
    const currentNotifications = this.notificationsSubject.value;
    this.notificationsSubject.next(currentNotifications.filter(n => n.id !== id));
  }
}
