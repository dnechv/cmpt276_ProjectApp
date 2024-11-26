#MemmoryConnect

MemoryConnect is a project developed by SFU students in CMPT 276: Introduction to Software Engineering (Fall 2024), as part of Group #3. 

This innovative Android app is designed to support caregivers in providing personalized care for individuals with dementia. By organizing and managing patient information, the app transforms caregiving into a more accessible and streamlined experience.

At the heart of the app is the Patient Timeline, a unique feature that visualizes each patient's life journey. Caregivers can manage multiple patients, with each timeline serving as a dedicated space to organize photos, videos, and music contributed by family members. These contributions help recreate meaningful moments from the patient’s life, enriching dementia therapy through memory stimulation and emotional connection.


### App1: Caregiver App  
The Caregiver App is designed to assist caregivers in organizing and managing information for individuals with dementia.  

**Key features include:**  
- Viewing patient details and photos.  
- Managing patient profiles.  
- Uploading profile photos.  
- Using a special caregiver code (storage reference ID) to allow family members to connect and upload files that the patient will view during therapy.  
- Real-time data synchronization with Firebase, displaying files shared by family members for a particular patient.  
- PIN protection to prevent patients from accessing the full list of patients from the main screen.  
- Enhanced accessibility with larger text layouts and high-contrast colors, designed for better user experience, especially for older patients with dementia.  

---

### App2: Family Member App  
The Family Member App allows family members to upload relevant files for patients.  

**Key features include:**  
- A signup feature for new users.  
- Viewing user login details.  
- Linking users to a particular patient via a provided code (storage reference ID).  
- Contributing information by uploading photos or adding comments to the storage.  
- Securely synchronizing data with Firebase.  


### Directory Structure ###

The repository is organized as follows:

\Project
/
├── app1/                 # All files related to the Caregiver App
│   ├── app/              # Main source code for App1
│   ├── build.gradle.kts  # App1-specific Gradle build script
│   └── ...               # Other files related to App1
├── app2/                 # All files related to the Family Member App
│   ├── app/              # Main source code for App2
│   ├── build.gradle.kts  # App2-specific Gradle build script
│   └── ...               # Other files related to App2
├── build.gradle.kts      # Root Gradle build script
├── settings.gradle.kts   # Gradle project settings
└── README.md             # This file



# Code Style Guide

1. Naming Conventions
- Classes: Use PascalCase (UserProfile).
- Methods: Use camelCase (calculateScore).
- Variables: Use camelCase (userAge).
- Constants: Use UPPER_CASE (MAX_LIMIT).

2. Formatting
- Line Length: Max 100 characters per line.
- Braces: Same Line Braces.

3. Comments
- Class and Method Comments**: Brief description of purpose.
- Inline Comments:logic explanation + organization 

4. File Structure
- Organize by feature 

5. Best Practices
- Write modular, reusable code.
