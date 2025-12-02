# Database Configuration

## Local Development (H2)
- **Type**: H2 (embedded file-based database)
- **Location**: `pc/backend/data/digiscribdb.mv.db`
- **Config**: `application.properties`
- **URL**: `jdbc:h2:file:./data/digiscribdb`
- **Console**: http://localhost:8081/h2-console

## Heroku Production (PostgreSQL)

### Automatic Setup
When you run:
```bash
heroku addons:create heroku-postgresql:essential-0 -a digiscribe-backend
```

Heroku automatically:
1. Creates a PostgreSQL database
2. Sets `DATABASE_URL` environment variable
3. Backend reads `DATABASE_URL` from `application-heroku.properties`

### Configuration
**File**: `pc/backend/src/main/resources/application-heroku.properties`
```properties
# Heroku auto-injects DATABASE_URL
spring.datasource.url=${DATABASE_URL}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Database URL Format
Heroku provides: `postgres://user:password@host:5432/dbname`

Spring Boot automatically converts it to JDBC format:
`jdbc:postgresql://host:5432/dbname?user=user&password=password`

### Tables Created Automatically
When backend starts, Hibernate creates:
- `users` - User accounts
- `prediction_history` - Prediction records
- `models` - ML model metadata
- `training_jobs` - Training job records

### Access Database
```bash
# Connect to Heroku PostgreSQL
heroku pg:psql -a digiscribe-backend

# View tables
\dt

# Query users
SELECT * FROM users;

# Query predictions
SELECT * FROM prediction_history LIMIT 10;

# Exit
\q
```

### Database Management
```bash
# View database info
heroku pg:info -a digiscribe-backend

# View database credentials
heroku config:get DATABASE_URL -a digiscribe-backend

# Reset database (WARNING: deletes all data)
heroku pg:reset DATABASE -a digiscribe-backend --confirm digiscribe-backend

# Backup database
heroku pg:backups:capture -a digiscribe-backend

# Download backup
heroku pg:backups:download -a digiscribe-backend
```

## Migration from H2 to PostgreSQL

If you have local data to migrate:

### Option 1: Manual Export/Import
```bash
# 1. Export from H2 (local)
# Connect to H2 console: http://localhost:8081/h2-console
# Run: SCRIPT TO 'backup.sql'

# 2. Convert H2 SQL to PostgreSQL format (manual editing needed)

# 3. Import to Heroku
heroku pg:psql -a digiscribe-backend < backup.sql
```

### Option 2: Fresh Start (Recommended)
Just deploy - Hibernate will create empty tables automatically.
Users will register fresh accounts on production.

## Database Differences

| Feature | H2 (Local) | PostgreSQL (Heroku) |
|---------|-----------|---------------------|
| Type | Embedded | Remote |
| Persistence | File-based | Cloud-hosted |
| Console | Built-in | CLI via `heroku pg:psql` |
| Cost | Free | Free tier (10K rows) |
| Performance | Fast (local) | Network latency |
| Backup | Manual file copy | Automatic daily backups |

## Free Tier Limits (Heroku Postgres Essential-0)

- **Rows**: 10,000 max
- **Storage**: 1 GB
- **Connections**: 20 max
- **Backups**: Daily automatic
- **Cost**: $0/month

If you exceed limits, upgrade to:
```bash
heroku addons:upgrade heroku-postgresql:mini -a digiscribe-backend
# $5/month, 10M rows, 10GB storage
```

## Verification After Deployment

```bash
# Check database is connected
heroku logs --tail -a digiscribe-backend | grep -i database

# Should see:
# "HikariPool-1 - Start completed"
# "Hibernate: create table users..."

# Test database
heroku pg:psql -a digiscribe-backend
\dt  # List tables
\q   # Exit
```

## Summary

✅ **Local**: H2 file database (no setup needed)  
✅ **Heroku**: PostgreSQL (auto-configured via addon)  
✅ **Migration**: Automatic via Hibernate DDL  
✅ **No manual SQL needed**: Tables created on first startup
