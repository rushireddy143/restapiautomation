# 📧 **Jenkins Outlook Email Configuration Guide**

## 🎯 **Overview**
This guide helps you configure Jenkins to send email notifications through Outlook/Office 365 SMTP server for your API automation pipeline.

## 🔧 **Jenkins Email Configuration**

### **1. Install Required Jenkins Plugins**
Navigate to **Manage Jenkins** → **Manage Plugins** → **Available** and install:
- ✅ **Email Extension Plugin** (emailext)
- ✅ **Mailer Plugin** 
- ✅ **Build User Vars Plugin** (for getting build user info)

### **2. Configure System Email Settings**

#### **A. Go to Manage Jenkins → Configure System**

#### **B. Configure Extended E-mail Notification**
```
SMTP Server: smtp.office365.com
SMTP Port: 587
Use SMTP Authentication: ✅ Checked
Username: your-jenkins-email@company.com
Password: your-app-password (see below)
Use SSL: ❌ Unchecked
Use TLS: ✅ Checked
```

#### **C. Configure E-mail Notification (Legacy)**
```
SMTP Server: smtp.office365.com
Default user e-mail suffix: @company.com
Use SMTP Authentication: ✅ Checked
Username: your-jenkins-email@company.com
Password: your-app-password
Use SSL: ❌ Unchecked
SMTP Port: 587
```

### **3. Outlook/Office 365 App Password Setup**

#### **For Office 365 Business Accounts:**
1. Go to **Microsoft 365 Admin Center**
2. Navigate to **Users** → **Active Users**
3. Select the Jenkins service account
4. Go to **Mail** tab → **Manage email apps**
5. Enable **Authenticated SMTP**

#### **For Personal Outlook Accounts:**
1. Go to **account.microsoft.com**
2. Navigate to **Security** → **Advanced security options**
3. Enable **App passwords**
4. Generate a new app password for Jenkins
5. Use this app password instead of your regular password

### **4. Test Email Configuration**
1. In Jenkins **Configure System** page
2. Scroll to **Extended E-mail Notification** section
3. Click **Test configuration by sending test e-mail**
4. Enter your email address
5. Click **Test configuration**

## 📧 **Enhanced Email Features Implemented**

### **Pre-Execution Email** ✅
- **Trigger**: Before test execution starts
- **Content**: Build parameters, execution plan, progress links
- **Recipients**: Configurable email list
- **Format**: Rich HTML with professional styling

### **Post-Execution Emails** ✅

#### **Success Email Features:**
- ✅ **Test Statistics**: Total, Passed, Failed, Skipped counts
- ✅ **Execution Details**: Environment, duration, success rate
- ✅ **Report Links**: Allure, TestNG, Console, Artifacts
- ✅ **Professional Styling**: Outlook-compatible HTML/CSS
- ✅ **Action Items**: Next steps and recommendations

#### **Failure Email Features:**
- ✅ **Detailed Statistics**: Comprehensive test breakdown
- ✅ **Failed Test List**: Top 5 failed tests with details
- ✅ **Investigation Links**: Console logs, reports, artifacts
- ✅ **Action Items**: Step-by-step troubleshooting guide
- ✅ **Log Attachments**: Automatic attachment of relevant logs

### **Unstable Build Email** ✅
- **Trigger**: When some tests fail but build continues
- **Content**: Partial failure analysis and recommendations
- **Attachments**: Test reports and logs

## 🎛️ **Pipeline Email Parameters**

### **Available Parameters:**
```groovy
NOTIFICATION_EMAILS: Additional email addresses (comma-separated)
SEND_PRE_EXECUTION_EMAIL: Enable/disable pre-execution notifications
SEND_DETAILED_REPORTS: Include detailed test results in emails
```

### **Usage Examples:**
```bash
# Run with additional email notifications
mvn test -DNOTIFICATION_EMAILS="manager@company.com,qa-team@company.com"

# Disable pre-execution emails
mvn test -DSEND_PRE_EXECUTION_EMAIL=false

# Enable detailed reporting
mvn test -DSEND_DETAILED_REPORTS=true
```

## 📊 **Email Content Features**

### **Professional HTML Styling** ✅
- **Outlook-Compatible CSS**: Tested with Outlook 2016/2019/365
- **Responsive Design**: Works on desktop and mobile
- **Corporate Branding**: Professional color scheme and layout
- **Grid Layout**: Statistics displayed in card format

### **Rich Content** ✅
- **Test Statistics Cards**: Visual representation of results
- **Execution Timeline**: Start/end times with duration
- **Interactive Links**: Direct access to reports and logs
- **Failure Analysis**: Detailed breakdown of issues
- **Action Items**: Clear next steps for team

### **Attachments** ✅
- **Success**: Test summary files
- **Failure**: Console logs, test reports, error logs
- **Unstable**: Partial test results and analysis

## 🔧 **Troubleshooting Email Issues**

### **Common Issues & Solutions:**

#### **1. Authentication Failures**
```
Error: 535 5.7.3 Authentication unsuccessful
Solution: 
- Verify app password is correct
- Enable "Authenticated SMTP" in Office 365
- Check username format (full email address)
```

#### **2. Connection Timeouts**
```
Error: Connection timed out
Solution:
- Verify SMTP server: smtp.office365.com
- Check port: 587 (not 25 or 465)
- Ensure TLS is enabled, SSL is disabled
```

#### **3. Emails Not Received**
```
Possible Causes:
- Check spam/junk folders
- Verify recipient email addresses
- Check Office 365 mail flow rules
- Verify sender reputation
```

#### **4. HTML Formatting Issues**
```
Solution:
- Use inline CSS for Outlook compatibility
- Test with different email clients
- Avoid complex CSS features
- Use table-based layouts
```

## 📋 **Email Configuration Checklist**

### **Jenkins Configuration** ✅
- [ ] Email Extension Plugin installed
- [ ] SMTP server configured (smtp.office365.com)
- [ ] Port set to 587
- [ ] TLS enabled, SSL disabled
- [ ] Authentication credentials configured
- [ ] Test email sent successfully

### **Outlook/Office 365 Configuration** ✅
- [ ] Service account created for Jenkins
- [ ] App password generated
- [ ] Authenticated SMTP enabled
- [ ] Mail flow rules configured (if needed)
- [ ] Sender reputation verified

### **Pipeline Configuration** ✅
- [ ] Email recipients configured
- [ ] Pre-execution notifications enabled
- [ ] Post-execution notifications configured
- [ ] Attachment patterns specified
- [ ] HTML formatting tested

## 🚀 **Advanced Email Features**

### **Conditional Notifications**
```groovy
// Send emails only for specific environments
when {
    anyOf {
        environment name: 'TEST_ENV', value: 'staging'
        environment name: 'TEST_ENV', value: 'prod'
    }
}
```

### **Custom Email Templates**
```groovy
// Use custom email templates
emailext (
    to: recipients,
    subject: customSubject,
    body: readFile('email-templates/success-template.html'),
    mimeType: 'text/html'
)
```

### **Team-Specific Notifications**
```groovy
// Different notifications for different teams
def devTeam = 'dev-team@company.com'
def qaTeam = 'qa-team@company.com'
def managementTeam = 'management@company.com'

// Send to appropriate teams based on results
if (testsPassed) {
    emailext(to: "${devTeam},${qaTeam}", ...)
} else {
    emailext(to: "${devTeam},${qaTeam},${managementTeam}", ...)
}
```

## 📈 **Email Analytics & Monitoring**

### **Track Email Delivery**
- Monitor Jenkins email logs
- Check Office 365 message trace
- Verify recipient engagement
- Analyze email open rates (if tracking enabled)

### **Email Performance Optimization**
- Optimize HTML for faster loading
- Compress attachments when possible
- Use CDN for images (if external images allowed)
- Monitor email size limits

Your Jenkins pipeline now has **comprehensive Outlook email notifications** with professional formatting and detailed reporting! 📧✨
