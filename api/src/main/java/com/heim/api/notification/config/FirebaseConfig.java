package com.heim.api.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {
    @Value("${firebase.admin.credentials}")
    private String firebaseAdminCredentials;

    public FirebaseConfig() {
        log.info("✅ FirebaseConfig se ha cargado en el contexto de Spring.");
    }

    @PostConstruct
    public void initializeFirebase(){
        try {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("holi-10c28-firebase-adminsdk-fbsvc-0a976d9ee8.json");
            log.info("✅ Se está inicializando Firebase...");
            if (serviceAccount == null){
                throw new IllegalStateException("No se pudo cargar el archivo holi-10c28-firebase-adminsdk-fbsvc-0a976d9ee8.json");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()){
                FirebaseApp.initializeApp(options);
                log.info("✅ Firebase Admin SDK inicializado correctamente.");
            }else{
                log.info("⚠️ Firebase ya estaba inicializado.");
            }
        }catch(IOException e ){
            log.error("❌ Error al inicializar Firebase: {}", e.getMessage(), e);
        }


    }
}
