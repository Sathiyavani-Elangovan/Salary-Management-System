export interface AuditLog {
  id: number;
  action: string;
  entityType: string;
  entityId?: number;
  userId?: number;
  username: string;
  oldValue?: string;
  newValue?: string;
  ipAddress?: string;
  createdAt: string;
}
