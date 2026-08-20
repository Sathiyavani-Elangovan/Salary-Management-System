# Salary Management System - Frontend

## Technology Stack

- **Framework**: Angular 17
- **Language**: TypeScript 5.x
- **UI Library**: Standalone components with custom styling
- **HTTP Client**: Angular HttpClient
- **Routing**: Angular Router
- **Styling**: SCSS

## Features

### Dashboard
- Real-time analytics and statistics
- Salary distribution visualization
- Department and country breakdowns
- Summary cards with key metrics

### Employee Management
- Paginated employee list (20 per page)
- Advanced search with debouncing
- Filter by department and country
- CRUD operations (Create, Read, Update, Delete)
- Soft delete with confirmation

### Employee Form
- Reactive forms with validation
- Real-time validation feedback
- Dropdown selects for departments, countries, etc.
- Date picker for date joined
- Create and edit modes

## Getting Started

### Prerequisites
- Node.js 18+ and npm
- Angular CLI 17 (`npm install -g @angular/cli`)

### Installation

```bash
# Install dependencies
npm install

# Start development server
ng serve

# OR with proxy configuration (recommended)
ng serve --proxy-config proxy.conf.json
```

The app will be available at **http://localhost:4200**

**Note**: Backend must be running on **http://localhost:8080** for API calls to work.

### Build for Production

```bash
# Build production bundle
ng build --configuration production

# Output will be in dist/frontend/
```

## Project Structure

```
src/
├── app/
│   ├── components/
│   │   ├── dashboard/           # Analytics dashboard
│   │   ├── employee-list/       # Employee table with filters
│   │   └── employee-form/       # Create/Edit employee form
│   ├── services/
│   │   ├── employee.service.ts  # Employee API service
│   │   └── analytics.service.ts # Analytics API service
│   ├── models/
│   │   └── employee.model.ts    # TypeScript interfaces
│   ├── app.component.ts         # Root component with navigation
│   ├── app.routes.ts            # Route configuration
│   └── app.config.ts            # App configuration
├── assets/                      # Static assets
├── styles.scss                  # Global styles
└── index.html                   # Main HTML file
```

## API Configuration

The app is configured to proxy API calls to the backend:

**proxy.conf.json**:
```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

All requests to `/api/*` are forwarded to `http://localhost:8080/api/*`

## Component Details

### Dashboard Component
- **Route**: `/dashboard`
- **Features**: 
  - Total employees, payroll, avg/median salary stats
  - Department breakdown with employee counts
  - Country breakdown with average salaries
  - Salary distribution bar chart

### Employee List Component
- **Route**: `/employees`
- **Features**:
  - Paginated table (20 records per page)
  - Search by name (debounced 300ms)
  - Filter by department and country
  - View, edit, delete actions
  - Clear filters button

### Employee Form Component
- **Routes**: 
  - `/employees/new` - Create new employee
  - `/employees/:id/edit` - Edit existing employee
- **Features**:
  - Reactive forms with validation
  - Required field validation
  - Email format validation
  - Number range validation
  - Real-time error messages

## Styling

- Custom SCSS with responsive design
- Mobile-friendly navigation menu
- Card-based layouts
- Consistent color scheme (#1976d2 primary)
- Smooth transitions and hover effects

## Testing

```bash
# Run unit tests
ng test

# Run tests with coverage
ng test --code-coverage

# View coverage report
open coverage/frontend/index.html
```

## Development

### Adding New Components

```bash
ng generate component components/new-component --standalone
```

### Adding New Services

```bash
ng generate service services/new-service
```

### Code Style

- Use standalone components
- Follow Angular style guide
- Use TypeScript strict mode
- Implement OnDestroy for subscriptions
- Use RxJS for reactive programming

## Performance Optimizations

- Debounced search input (300ms)
- Lazy loading routes (future enhancement)
- OnPush change detection (future enhancement)
- Virtual scrolling for large lists (future enhancement)
- HTTP caching (future enhancement)

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Future Enhancements

- [ ] Add Angular Material components
- [ ] Implement virtual scrolling for large datasets
- [ ] Add chart visualizations with Chart.js
- [ ] Implement bulk operations UI
- [ ] Add export to CSV functionality
- [ ] Implement advanced filtering
- [ ] Add loading skeletons
- [ ] Progressive Web App (PWA) features
- [ ] Dark mode toggle
- [ ] Accessibility improvements

## Troubleshooting

### API calls not working
- Ensure backend is running on port 8080
- Check proxy configuration in proxy.conf.json
- Start Angular with: `ng serve --proxy-config proxy.conf.json`

### CORS errors
- Backend has CORS enabled for localhost:4200
- Check backend application.yml configuration

### Build errors
- Clear node_modules: `rm -rf node_modules && npm install`
- Clear Angular cache: `ng cache clean`
- Check Node.js version: `node --version` (should be 18+)

## License

Copyright © 2026 ACME Corporation
