FROM gradle:9.7.1-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN gradle :server:installDist --no-daemon

FROM eclipse-temurin:17-jre-jammy
RUN useradd --system --uid 10001 --create-home app
WORKDIR /app
COPY --from=build /workspace/server/build/install/server/ ./
RUN chown -R app:app /app
USER app
ENV HOST=0.0.0.0
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["./bin/server"]
