//! # synth lore generated 
//! Simple Axum Web Server
//!
//! This crate provides a simple, secure web server built with the Axum framework.
//! It serves a static home page, a custom 404 page, and static assets (CSS).

use axum::{
    http::{header, HeaderValue, StatusCode},
    response::{Html, IntoResponse},
    Router,
};
use std::net::SocketAddr;
use tower_http::{
    services::ServeDir,
    set_header::SetResponseHeaderLayer,
};
use tracing::info;
use tracing_subscriber::{layer::SubscriberExt, util::SubscriberInitExt};

// --- Main Application Entry Point ---

#[tokio::main]
async fn main() {
    // Initialize tracing for logging.
    tracing_subscriber::registry()
        .with(
            tracing_subscriber::EnvFilter::try_from_default_env()
                .unwrap_or_else(|_| "simple_axum_server=info".into()),
        )
        .with(tracing_subscriber::fmt::layer())
        .init();

    // The application router is built here.
    let app = app();

    // Define the address to bind the server to.
    let port = std::env::var("PORT").unwrap_or_else(|_| "3000".to_string());
    let addr: SocketAddr = format!("0.0.0.0:{}", port)
        .parse()
        .expect("Invalid address format");

    info!("Starting server and listening on {}", addr);

    // Create a listener and serve the application.
    let listener = tokio::net::TcpListener::bind(addr).await.unwrap();
    axum::serve(listener, app).await.unwrap();
}

// --- Application Router ---

fn app() -> Router {
    // ServeDir serves files from the "public" directory.
    let serve_dir = ServeDir::new("public");

    Router::new()
        .nest_service("/public", serve_dir)
        .fallback(not_found_handler)
        // --- Middleware Layers ---
        // Layers are executed from bottom to top (when processing the request)
        // or top to bottom (when processing the response).
        
        // 1. Content-Type (Default to HTML)
        .layer(SetResponseHeaderLayer::if_not_present(
            header::CONTENT_TYPE,
            HeaderValue::from_static("text/html; charset=utf-8"),
        ))
        // 2. Strict-Transport-Security
        .layer(SetResponseHeaderLayer::if_not_present(
            header::STRICT_TRANSPORT_SECURITY,
            HeaderValue::from_static("max-age=31536000; includeSubDomains"),
        ))
        // 3. X-Frame-Options
        .layer(SetResponseHeaderLayer::if_not_present(
            header::X_FRAME_OPTIONS,
            HeaderValue::from_static("DENY"),
        ))
        // 4. X-Content-Type-Options
        .layer(SetResponseHeaderLayer::if_not_present(
            header::X_CONTENT_TYPE_OPTIONS,
            HeaderValue::from_static("nosniff"),
        ))
        // 5. Content-Security-Policy
        .layer(SetResponseHeaderLayer::if_not_present(
            header::CONTENT_SECURITY_POLICY,
            HeaderValue::from_static(
                "default-src 'none'; style-src 'self' 'unsafe-inline'; img-src 'self'; frame-ancestors 'none'",
            ),
        ))
}

// --- Route Handlers ---

async fn not_found_handler(uri: axum::http::Uri) -> impl IntoResponse {
    if uri.path() == "/" {
        (StatusCode::OK, Html(layout("X Y Z Z Y")))
    } else {
        let body = layout("You have entered the void. There is no one here. There is no place to go. There is no lamp.");
        (StatusCode::NOT_FOUND, Html(body))
    }
}

// --- HTML Layout ---

fn layout(content: &str) -> String {
    format!(
        r#"<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Simple Web Server</title>
    <link rel="stylesheet" href="/public/css/style.css">
</head>
<body>
    <div class="content">
        <h1>{}</h1>
    </div>
</body>
</html>"#,
        content
    )
}
