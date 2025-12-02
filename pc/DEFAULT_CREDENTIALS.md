# Default Admin Credentials

## Admin Account

**Username**: `admin`  
**Password**: `admin123`  
**Email**: `admin@digiscrib.com`  
**Role**: `ADMIN`

## How It Works

The backend automatically creates this admin user on first startup if it doesn't exist.

**Code**: `pc/backend/src/main/java/com/digiscrib/DigiScribApplication.java`

```java
if (userRepository.findByUsername("admin").isEmpty()) {
    User admin = new User();
    admin.setUsername("admin");
    admin.setEmail("admin@digiscrib.com");
    admin.setPassword(passwordEncoder.encode("admin123"));
    admin.setRole("ADMIN");
    userRepository.save(admin);
}
```

## Login Flow

1. Open frontend: `https://digiscribe-frontend.herokuapp.com`
2. Click "Login" (not visible until you navigate)
3. Enter credentials:
   - Username: `admin`
   - Password: `admin123`
4. Access admin panel and all features

## Security Note

⚠️ **IMPORTANT**: Change the default password after first login in production!

To change password, you can:
1. Add a "Change Password" feature in Settings
2. Or manually update via Heroku PostgreSQL:

```bash
heroku pg:psql -a digiscribe-backend

UPDATE users SET password = '<bcrypt_hash>' WHERE username = 'admin';
```

## Creating Additional Users

Users can register via the signup page inside the application.

New users will have `USER` role by default (not `ADMIN`).
