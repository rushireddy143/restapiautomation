package com.api.automation.utils;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.Date;

/**
 * Test class for email notification functionality
 * Tests email sending capabilities with Outlook/Office 365
 */
public class EmailNotificationTest {
    
    private static final Logger logger = LogManager.getLogger(EmailNotificationTest.class);
    
    private String smtpHost = "smtp.office365.com";
    private String smtpPort = "587";
    private String username = "jenkins@company.com"; // Replace with your email
    private String password = "your-app-password"; // Replace with your app password
    private String fromEmail = "jenkins@company.com";
    private String toEmail = "your-email@company.com"; // Replace with recipient email
    
    @BeforeClass
    public void setupEmailConfig() {
        // Load email configuration from system properties if available
        smtpHost = System.getProperty("email.smtp.host", smtpHost);
        smtpPort = System.getProperty("email.smtp.port", smtpPort);
        username = System.getProperty("email.username", username);
        password = System.getProperty("email.password", password);
        fromEmail = System.getProperty("email.from", fromEmail);
        toEmail = System.getProperty("email.to", toEmail);
        
        logger.info("Email configuration loaded - SMTP Host: {}, Port: {}", smtpHost, smtpPort);
    }
    
    @Test(groups = {"email", "notification"})
    public void testBasicEmailSending() {
        logger.info("Testing basic email sending functionality");
        
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("✅ API Automation - Email Test Successful");
            message.setSentDate(new Date());
            
            String emailBody = """
                <html>
                <body style="font-family: Arial, sans-serif; margin: 20px;">
                    <h2 style="color: #4CAF50;">📧 Email Notification Test</h2>
                    <p>This is a test email from the API Automation Framework.</p>
                    <p><strong>Test Details:</strong></p>
                    <ul>
                        <li>SMTP Server: %s</li>
                        <li>Port: %s</li>
                        <li>Authentication: Enabled</li>
                        <li>TLS: Enabled</li>
                        <li>Sent At: %s</li>
                    </ul>
                    <p style="color: #4CAF50;"><strong>✅ Email configuration is working correctly!</strong></p>
                </body>
                </html>
                """.formatted(smtpHost, smtpPort, new Date());
            
            message.setContent(emailBody, "text/html; charset=utf-8");
            
            Transport.send(message);
            
            logger.info("✅ Test email sent successfully to: {}", toEmail);
            
        } catch (MessagingException e) {
            logger.error("❌ Failed to send test email", e);
            throw new RuntimeException("Email sending test failed", e);
        }
    }
    
    @Test(groups = {"email", "notification"})
    public void testPreExecutionEmailFormat() {
        logger.info("Testing pre-execution email format");
        
        try {
            String preExecutionEmail = generatePreExecutionEmail();
            sendTestEmail("🚀 API Test Execution STARTED - Test Format", preExecutionEmail);
            logger.info("✅ Pre-execution email format test completed");
            
        } catch (Exception e) {
            logger.error("❌ Pre-execution email format test failed", e);
            throw new RuntimeException("Pre-execution email test failed", e);
        }
    }
    
    @Test(groups = {"email", "notification"})
    public void testSuccessEmailFormat() {
        logger.info("Testing success email format");
        
        try {
            String successEmail = generateSuccessEmail();
            sendTestEmail("✅ API Tests PASSED - Test Format", successEmail);
            logger.info("✅ Success email format test completed");
            
        } catch (Exception e) {
            logger.error("❌ Success email format test failed", e);
            throw new RuntimeException("Success email test failed", e);
        }
    }
    
    @Test(groups = {"email", "notification"})
    public void testFailureEmailFormat() {
        logger.info("Testing failure email format");
        
        try {
            String failureEmail = generateFailureEmail();
            sendTestEmail("❌ API Tests FAILED - Test Format", failureEmail);
            logger.info("✅ Failure email format test completed");
            
        } catch (Exception e) {
            logger.error("❌ Failure email format test failed", e);
            throw new RuntimeException("Failure email test failed", e);
        }
    }
    
    private void sendTestEmail(String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setSentDate(new Date());
        message.setContent(body, "text/html; charset=utf-8");
        
        Transport.send(message);
    }
    
    private String generatePreExecutionEmail() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 15px; border-radius: 5px; }
                    .content { padding: 20px; border: 1px solid #ddd; border-radius: 5px; margin-top: 10px; }
                    .info-table { width: 100%; border-collapse: collapse; margin: 15px 0; }
                    .info-table th, .info-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    .info-table th { background-color: #f2f2f2; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h2>🚀 API Test Execution Started</h2>
                </div>
                <div class="content">
                    <p>Hello Team,</p>
                    <p>The API automation test execution has been initiated.</p>
                    <table class="info-table">
                        <tr><th>Parameter</th><th>Value</th></tr>
                        <tr><td>Environment</td><td>TEST</td></tr>
                        <tr><td>Test Suite</td><td>collection-tests.xml</td></tr>
                        <tr><td>Started At</td><td>%s</td></tr>
                    </table>
                    <p>You will receive another notification once execution is completed.</p>
                </div>
            </body>
            </html>
            """.formatted(new Date());
    }
    
    private String generateSuccessEmail() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
                    .container { max-width: 800px; margin: 0 auto; background-color: white; border-radius: 10px; overflow: hidden; }
                    .header { background: linear-gradient(135deg, #4CAF50, #45a049); color: white; padding: 30px; text-align: center; }
                    .content { padding: 30px; }
                    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin: 20px 0; }
                    .stat-card { background: #f8f9fa; padding: 20px; border-radius: 8px; text-align: center; border-left: 4px solid #4CAF50; }
                    .stat-number { font-size: 32px; font-weight: bold; color: #4CAF50; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✅ API Tests Completed Successfully!</h1>
                    </div>
                    <div class="content">
                        <div class="stats-grid">
                            <div class="stat-card">
                                <div class="stat-number">25</div>
                                <div>Total Tests</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number">25</div>
                                <div>Passed</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number">0</div>
                                <div>Failed</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number">0</div>
                                <div>Skipped</div>
                            </div>
                        </div>
                        <p><strong>🎉 Congratulations!</strong> All API tests executed successfully.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    private String generateFailureEmail() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
                    .container { max-width: 800px; margin: 0 auto; background-color: white; border-radius: 10px; overflow: hidden; }
                    .header { background: linear-gradient(135deg, #f44336, #d32f2f); color: white; padding: 30px; text-align: center; }
                    .content { padding: 30px; }
                    .failed-tests { background: #ffebee; padding: 20px; border-radius: 8px; border-left: 4px solid #f44336; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>❌ API Tests Failed</h1>
                    </div>
                    <div class="content">
                        <div class="failed-tests">
                            <h3>🔍 Failed Tests:</h3>
                            <ul>
                                <li>testCreateUserFromCollection</li>
                                <li>testUpdateUserPutFromCollection</li>
                            </ul>
                        </div>
                        <p><strong>⚠️ Attention Required:</strong> Please investigate and resolve the issues.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}
