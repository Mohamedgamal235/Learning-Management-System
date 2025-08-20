# Learning Management System - Entity Framework Configurations

This document provides an overview of all Entity Framework configurations for the Learning Management System models.

## Configuration Overview

### ✅ Completed Configurations

#### User Management
- **UserConfiguration.cs** - Base abstract User class with inheritance setup
- **AdminConfiguration.cs** - Admin-specific properties
- **InstructorConfiguration.cs** - Instructor-specific properties and relationships
- **StudentConfiguration.cs** - Student-specific properties and relationships

#### Course Management
- **CourseConfiguration.cs** - Course entity with all relationships
- **LessonConfiguration.cs** - Lesson entity with ordering and relationships
- **CourseMaterialConfiguration.cs** - Course materials with file handling
- **LessonVideoConfiguration.cs** - Video content for lessons

#### Assessment System
- **QuizConfiguration.cs** - Quiz entity with time constraints
- **QuestionConfiguration.cs** - Abstract question with inheritance
- **MCQQuestionConfiguration.cs** - Multiple choice questions
- **TrueFalseConfiguration.cs** - True/False questions
- **ChoiceConfiguration.cs** - Answer choices for MCQs
- **QuizAttemptConfiguration.cs** - Student quiz attempts
- **StudentAnswerConfiguration.cs** - Individual student answers

#### Assignment System
- **AssignmentConfiguration.cs** - Course assignments
- **SubmissionConfiguration.cs** - Student submissions with grading

#### Attendance System
- **AttendanceConfiguration.cs** - Student attendance tracking

#### Enrollment System
- **EnrollmentConfiguration.cs** - Student course enrollments

#### Notification System
- **NotificationConfiguration.cs** - User notifications

## Key Features Implemented

### 1. Inheritance Configuration
- **User Hierarchy**: Admin, Instructor, Student inherit from User
- **Question Hierarchy**: MCQQuestion, TrueFalseQuestion inherit from Question
- Proper discriminator configuration for TPH (Table Per Hierarchy)

### 2. Relationship Management
- **One-to-Many**: Course → Lessons, Assignments, Quizzes
- **Many-to-Many**: Students ↔ Courses (via Enrollment)
- **Composite Keys**: Attendance, Submission, Enrollment
- **Cascade Delete**: Appropriate cascade behaviors for related entities

### 3. Data Constraints
- **Unique Constraints**: Email, Username for users
- **Check Constraints**: 
  - Quiz end date > start date
  - Assignment deadline > creation date
  - Grade ranges (0-100)
  - Positive values for file sizes, order indexes
  - Salary validation for instructors

### 4. Property Constraints
- **Required Fields**: Essential properties marked as required
- **String Length Limits**: Appropriate max lengths for text fields
- **Default Values**: Grade defaults to 0 for submissions

## Database Schema Highlights

### User Management
```sql
-- Users table with discriminator for inheritance
Users (UserId, UserName, Email, Password, Role, ...)
-- Role discriminator: Admin, Instructor, Student
```

### Course Structure
```sql
Courses (CourseId, Title, Description, InstructorId, ...)
Lessons (LessonId, Title, OrderIndex, CourseId, ...)
CourseMaterials (MaterialId, Title, FilePath, CourseId, LessonId, ...)
```

### Assessment System
```sql
Quizzes (QuizId, Title, StartDateTime, EndDateTime, CourseId, ...)
Questions (QuestionId, QuestionText, QuestionType, QuizId, ...)
-- QuestionType discriminator: MCQ, TrueFalse
```

### Student Progress
```sql
Enrollments (CourseId, StudentId, EnrolledAt, ProgressPercent, ...)
Submissions (AssignmentId, StudentId, FilePath, Grade, ...)
QuizAttempts (AttemptId, QuizId, StartAt, SubmittedAt, Total, ...)
```

## Usage Notes

1. **Migration Generation**: All configurations are automatically applied via `ApplyConfigurationsFromAssembly()`
2. **Validation**: Check constraints ensure data integrity at the database level
3. **Performance**: Proper indexing on frequently queried fields (Email, UserName)
4. **Maintainability**: Each entity has its own configuration file for easy maintenance

## Next Steps

1. Generate and apply database migrations
2. Add seed data for testing
3. Implement repository pattern for data access
4. Add additional business logic validation
5. Consider adding audit trails for important entities
