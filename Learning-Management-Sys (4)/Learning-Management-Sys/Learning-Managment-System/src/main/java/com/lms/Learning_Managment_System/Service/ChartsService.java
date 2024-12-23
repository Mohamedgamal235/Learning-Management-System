package com.lms.Learning_Managment_System.Service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lms.Learning_Managment_System.Model.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChartsService {
    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private student_coursesService studentCoursesService;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public void generateAssignmentChart(String courseTitle) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Assignment> assignments = assignmentService.getAssignments(courseTitle);
        List<enrolled_student> enrolledStudents = studentCoursesService.getStudentsEnrolledInCourse(courseTitle);
        int totalStudents = enrolledStudents.size();
        for (Assignment assignment : assignments) {
            dataset.addValue(assignment.getSubmissions().size(), "Submitted", assignment.getAssessmentName());
            dataset.addValue(totalStudents, "Total Students", assignment.getAssessmentName());
        }
        JFreeChart barChart = ChartFactory.createBarChart(
                "Assignment Completion Status",
                "Assignments",
                "Number of Students",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        barChart.setBackgroundPaint(Color.white);
        barChart.getTitle().setPaint(Color.black);
        ensureDirectoryExists("./charts");
        ChartUtils.saveChartAsPNG(
                new File("./charts/" + courseTitle + "_assignments.png"),
                barChart,
                WIDTH,
                HEIGHT
        );
    }
    public void generateAverageGradesChart(String courseTitle) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Assignment> assignments = assignmentService.getAssignments(courseTitle);

        for (Assignment assignment : assignments) {
            double totalGrades = 0;
            int gradedSubmissions = 0;
            for (assignmentSubmission submission : assignment.getSubmissions()) {
                String grade = submission.getGrade();
                if (grade != null) {
                    totalGrades += Double.parseDouble(grade);
                    gradedSubmissions++;
                }
            }
            double averageGrade = gradedSubmissions > 0 ? totalGrades / gradedSubmissions : 0;
            dataset.addValue(averageGrade, "Average Grade", assignment.getAssessmentName());
        }
        JFreeChart barChart = ChartFactory.createBarChart(
                "Average Grades for Assignments",
                "Assignments",
                "Average Grade",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        barChart.setBackgroundPaint(Color.white);
        barChart.getTitle().setPaint(Color.black);
        ensureDirectoryExists("./charts");
        ChartUtils.saveChartAsPNG(
                new File("./charts/" + courseTitle + "_average_grades.png"),
                barChart,
                WIDTH,
                HEIGHT
        );
    }

    public void generateCourseCompletionRateChart(String courseTitle) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Assignment> assignments = assignmentService.getAssignments(courseTitle);
        List<enrolled_student> enrolledStudents = studentCoursesService.getStudentsEnrolledInCourse(courseTitle);
        int totalAssignments = assignments.size();
        int totalStudents = enrolledStudents.size();
        int expectedTotalSubmissions = totalAssignments * totalStudents;
        int actualSubmissions = 0;
        int gradedSubmissions = 0;

        for (Assignment assignment : assignments) {
            List<assignmentSubmission> submissions = assignment.getSubmissions();
            actualSubmissions += submissions.size();
            for (assignmentSubmission submission : submissions) {
                if (submission.getGrade() != null) {
                    gradedSubmissions++;
                }
            }
        }

        double submissionRate = expectedTotalSubmissions > 0 ?
                ((double) actualSubmissions / expectedTotalSubmissions) * 100 : 0;
        double gradingRate = actualSubmissions > 0 ?
                ((double) gradedSubmissions / actualSubmissions) * 100 : 0;

        dataset.addValue(submissionRate, "Submission Rate", courseTitle);
        dataset.addValue(gradingRate, "Grading Rate", courseTitle);

        JFreeChart barChart = ChartFactory.createBarChart(
                "Course Progress Rates",
                "Course",
                "Rate (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        barChart.setBackgroundPaint(Color.white);
        barChart.getTitle().setPaint(Color.black);
        ensureDirectoryExists("./charts");
        ChartUtils.saveChartAsPNG(
                new File("./charts/" + courseTitle + "_course_completion_rate.png"),
                barChart,
                WIDTH,
                HEIGHT
        );
    }
    private void ensureDirectoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}