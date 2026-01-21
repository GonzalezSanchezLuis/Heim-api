package com.heim.api.auth.application.service;

import com.heim.api.auth.domain.entity.PasswordReset;
import com.heim.api.auth.infraestructure.repository.PasswordResetRepository;
import com.heim.api.users.domain.entity.User;
import com.heim.api.users.infraestructure.repository.UserRepository;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class EmailService {

    private final Resend resend;
    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;

    @Autowired
    public EmailService(
            @Value("${resend.api.key}") String apiKey,
            PasswordResetRepository passwordResetRepository,
            UserRepository userRepository
    ){
        this.resend = new Resend(apiKey);
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
    }

    public void sendWelcomeEmail(String toEmail, String firstName) {
        String brandColor = "#4F46E5";


        String htmlContent = String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<body style='margin: 0; padding: 0; background-color: #F0F2F5; font-family: \"Segoe UI\", Tahoma, Geneva, Verdana, sans-serif;'>" +
                        "  <table width='100%%' border='0' cellspacing='0' cellpadding='0'>" +
                        "    <tr>" +
                        "      <td align='center' style='padding: 40px 0;'>" +
                        "        <table width='500' border='0' cellspacing='0' cellpadding='0' style='background-color: #ffffff; border: 2px solid #E5E7EB; border-radius: 24px; overflow: hidden;'>" +
                        "          " +
                        "          <tr>" +
                        "            <td align='center' style='padding: 40px 40px 20px 40px;'>" +
                        "              <h1 style='color: #111827; font-size: 28px; font-weight: 800; margin: 0;'>¡Hola, %s!</h1>" +
                        "            </td>" +
                        "          </tr>" +
                        "          " +
                        "          <tr>" +
                        "            <td style='padding: 0 40px 30px 40px; text-align: center; color: #4B5563;'>" +
                        "              <p style='font-size: 18px; line-height: 1.6; margin: 0;'>" +
                        "                ¡Estamos muy emocionados de tenerte en <strong>Heim</strong>! Has dado el primer paso para organizar mejor tu mundo." +
                        "              </p>" +
                        "            </td>" +
                        "          </tr>" +
                        "          " +
                        "          <tr>" +
                        "            <td align='center' style='padding-bottom: 40px;'>" +
                        "              <a href='%s' style='background-color: %s; color: #ffffff; padding: 16px 32px; text-decoration: none; border-radius: 16px; font-weight: bold; font-size: 18px; display: inline-block; border-bottom: 4px solid #3730A3; transition: all 0.2s;'>" +
                        "                EMPEZAR AHORA" +
                        "              </a>" +
                        "            </td>" +
                        "          </tr>" +
                        "          " +
                        "          <tr>" +
                        "            <td style='padding: 20px; background-color: #F9FAFB; text-align: center; color: #9CA3AF; font-size: 14px; border-top: 2px solid #E5E7EB;'>" +
                        "              <p style='margin: 0;'>Enviado con ❤️ por el equipo de Heim</p>" +
                        "            </td>" +
                        "          </tr>" +
                        "        </table>" +
                        "        <p style='margin-top: 20px; color: #9CA3AF; font-size: 12px;'>© 2025 Heim Project. Todos los derechos reservados.</p>" +
                        "      </td>" +
                        "    </tr>" +
                        "  </table>" +
                        "</body>" +
                        "</html>",
                firstName,  brandColor
        );

        sendEmail(toEmail, "¡Te damos la bienvenida a Heim!", htmlContent);
    }

    private void sendEmail(String to, String subject, String html) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Heim <onboarding@resend.dev>")
                .to(to)
                .subject(subject)
                .html(html)
                .build();
        try {
            resend.emails().send(params);
        } catch (Exception e) {
            System.err.println("Error enviando correo: " + e.getMessage());
        }
    }

    public void createPasswordResetToken(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()){
            User user = userOptional.get();

            String token = UUID.randomUUID().toString();
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(30);

            PasswordReset passwordReset = new PasswordReset();
            passwordReset.setToken(token);
            passwordReset.setExpirationTime(expirationTime);
            passwordReset.setUser(user);

            passwordResetRepository.save(passwordReset);

            sendResetEmail(user.getEmail(), user.getFullName(), token);
        }else {
            System.out.println("Intento de recuperación para email no registrado: " + email);
        }


    }

    private void sendResetEmail(String toEmail, String firstName,String token) {
        String resetLink = "https://heimapp.com.co/reset-password?token=" + token;
        String brandColor = "#4F46E5";

        String htmlContent = String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<body style='margin: 0; padding: 0; background-color: #f4f7f9; font-family: Arial, sans-serif;'>" +
                        "  <table width='100%%' border='0' cellspacing='0' cellpadding='0'>" +
                        "    <tr>" +
                        "      <td align='center' style='padding: 20px 0;'>" +
                        "        <table width='600' border='0' cellspacing='0' cellpadding='0' style='background-color: #ffffff; border-radius: 8px; overflow: hidden;'>" +
                        "          <tr>" +
                        "            <td style='padding: 40px; text-align: left; color: #333333;'>" +
                        "              <h1 style='font-size: 24px;'>Hola, %s!</h1>" + // Marcador 1: firstName
                        "              <p style='font-size: 16px;'>Has solicitado restablecer tu contraseña. Haz clic en el botón de abajo para continuar:</p>" +
                        "            </td>" +
                        "          </tr>" +
                        "          <tr>" +
                        "            <td align='center' style='padding: 20px 0;'>" +
                        "              <a href='%s' style='background-color: %s; color: #ffffff; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>" + // Marcadores 2 y 3: resetLink y brandColor
                        "                Restablecer contraseña" +
                        "              </a>" +
                        "            </td>" +
                        "          </tr>" +
                "                       <td style='padding: 20px 40px; background-color: #f9fafb; text-align: center; color: #9ca3af; font-size: 12px;'>" +
                        "              <p>Si no solicitaste este cambio, puedes ignorar este correo de forma segura.</p>" +
                        "              <p>&copy; 2026 Heim Project. Todos los derechos reservados. Bogotá D.C </p>" +
                        "            </td>" +
                        "          </tr>" +
                        "        </table>" +
                        "      </td>" +
                        "    </tr>" +
                        "  </table>" +
                        "</body>" +
                        "</html>",
                firstName, resetLink, brandColor);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Heim App <onboarding@resend.dev>")
                .to(toEmail)
                .subject("Recupera tu contraseña")
                .html(htmlContent)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("Correo enviado con éxito vía Resend. ID: " + data.getId());
        } catch (ResendException e) {
            System.err.println("Error al enviar correo con Resend: " + e.getMessage());
        }
    }


    public void updatePassword(String token, String newPassword) {
        // 1. Buscar el token y verificar validez
        PasswordReset resetRequest = passwordResetRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o no encontrado"));

        if (resetRequest.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }

        // 2. Obtener al usuario y encriptar la nueva clave
        User user = resetRequest.getUser();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(newPassword));

        // 3. Guardar usuario y borrar el token para que no se use de nuevo
        userRepository.save(user);
        passwordResetRepository.delete(resetRequest);

        System.out.println("Contraseña actualizada para: " + user.getEmail());
    }

    }

