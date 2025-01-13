# Stage 1: Build
FROM node:20-alpine AS builder

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json .

# Install production dependencies only
RUN npm install --only=production

# Copy application source code
COPY . .

# Stage 2: Production
FROM gcr.io/distroless/nodejs:20

# Set the working directory
WORKDIR /app

# Copy only the necessary files from the build stage
COPY --from=builder /app .

# Run the application
CMD ["node","server.js"] 


 