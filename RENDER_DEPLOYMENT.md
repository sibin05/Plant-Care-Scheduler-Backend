# Render Deployment Guide

## Prerequisites
1. Create a Render account at https://render.com
2. Set up a MySQL database (you can use Render's PostgreSQL or external MySQL service)
3. Have your GitHub repository ready

## Steps to Deploy

### 1. Database Setup
- Create a MySQL database on a cloud provider (AWS RDS, Google Cloud SQL, or PlanetScale)
- Note down the connection details: host, port, database name, username, password

### 2. Push to GitHub
```bash
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

### 3. Deploy on Render
1. Go to https://dashboard.render.com
2. Click "New +" → "Web Service"
3. Connect your GitHub repository
4. Select your repository and branch
5. Configure the service:
   - **Name**: plant-care-backend
   - **Environment**: Docker
   - **Plan**: Free (or paid for better performance)
   - **Dockerfile Path**: ./Dockerfile

### 4. Set Environment Variables
In the Render dashboard, add these environment variables:

**Required Variables:**
- `DATABASE_URL`: jdbc:mysql://your-host:3306/your-database
- `DB_USERNAME`: your-database-username
- `DB_PASSWORD`: your-database-password
- `JWT_SECRET`: bXlTZWNyZXRLZXkxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=

**Optional (for email functionality):**
- `MAIL_USERNAME`: your-email@gmail.com
- `MAIL_PASSWORD`: your-gmail-app-password

### 5. Deploy
- Click "Create Web Service"
- Render will automatically build and deploy your application
- The build process takes 5-10 minutes

### 6. Access Your Application
- Once deployed, you'll get a URL like: https://plant-care-backend.onrender.com
- Health check endpoint: https://plant-care-backend.onrender.com/actuator/health

## Important Notes

### Database Considerations
- **Free Tier Limitation**: Render free tier sleeps after 15 minutes of inactivity
- **Database Connection**: Use a cloud MySQL service (not localhost)
- **Connection Pooling**: Consider adding HikariCP configuration for better performance

### Security
- Never commit sensitive data to GitHub
- Use Render's environment variables for all secrets
- The JWT secret should be a strong, randomly generated string

### Performance Tips
- Consider upgrading to a paid plan for production use
- Monitor your application logs in Render dashboard
- Set up proper logging levels for production

## Troubleshooting

### Common Issues
1. **Build Fails**: Check Java version compatibility (using Java 17)
2. **Database Connection**: Verify DATABASE_URL format and credentials
3. **Port Issues**: Application uses PORT environment variable (defaults to 8080)
4. **Health Check Fails**: Ensure /actuator/health endpoint is accessible

### Logs
- Check build logs in Render dashboard
- Monitor application logs for runtime issues
- Use `spring.jpa.show-sql=false` in production to reduce log noise

## Alternative: Manual Deployment
If you prefer not to use render.yaml, you can deploy manually through the Render dashboard by following steps 3-6 above.