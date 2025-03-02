FROM nginx:alpine

# Copy the built app
COPY ./dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf.template

# Copy and set permissions for entrypoint script
COPY docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s CMD wget -q -O /dev/null http://localhost || exit 1

# Set environment variable with a default value
ENV BACKEND_URL=http://localhost:8080

# Use custom entrypoint script
ENTRYPOINT ["/docker-entrypoint.sh"]
