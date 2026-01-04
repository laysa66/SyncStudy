package com.syncstudy.UI.ProfileManager;

import com.syncstudy.BL.ProfileManager.UserProfile;
import com.syncstudy.BL.SessionManager.SessionFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for viewing other users' profiles
 */
public class OtherProfilesController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortByCombo;
    @FXML private TableView<UserProfile> profilesTable;
    @FXML private TableColumn<UserProfile, String> nameColumn;
    @FXML private TableColumn<UserProfile, String> emailColumn;
    @FXML private TableColumn<UserProfile, String> universityColumn;
    @FXML private Pagination pagination;
    @FXML private Label totalUsersLabel;

    private UserDashboardController dashboardController;
    private SessionFacade session;
    private ObservableList<UserProfile> profilesList;
    private static final int PAGE_SIZE = 20;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        session = SessionFacade.getInstance();
        profilesList = FXCollections.observableArrayList();

        setupFilters();
        setupTable();
        setupPagination();
        loadProfiles();
    }

    /**
     * Set the parent dashboard controller
     */
    public void setDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
    }

    /**
     * Setup filter controls
     */
    private void setupFilters() {
        // Sort by
        sortByCombo.setItems(FXCollections.observableArrayList("Name", "First Name", "Last Name"));
        sortByCombo.setValue("Name");
        sortByCombo.setOnAction(e -> loadProfiles());

        // Search field listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            loadProfiles();
        });
    }

    /**
     * Setup table columns
     */
    private void setupTable() {
        // Name column - show full name
        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFullName()));

        // Email column - need to get from User object
        emailColumn.setCellValueFactory(data -> {
            // You'll need to enhance this - for now just show "-"
            return new SimpleStringProperty("-");
        });

        // University column - need to get from User object
        universityColumn.setCellValueFactory(data -> {
            // You'll need to enhance this - for now just show "-"
            return new SimpleStringProperty("-");
        });

        profilesTable.setItems(profilesList);
    }

    /**
     * Setup pagination
     */
    private void setupPagination() {
        pagination.setPageFactory(pageIndex -> {
            loadProfilesForPage(pageIndex);
            return new VBox(); // Return empty node, table is updated separately
        });
    }

    /**
     * Load profiles with current filters
     */
    private void loadProfiles() {
        loadProfilesForPage(0);
        updatePagination();
    }

    /**
     * Load profiles for a specific page
     */
    private void loadProfilesForPage(int page) {
        String search = searchField.getText();
        String sortBy = getSortByValue();

        // Get all profiles
        List<UserProfile> allProfiles = session.findAllProfiles(search, sortBy, page, PAGE_SIZE);

        // Filter out the current user's profile
        Long currentUserId = session.getCurrentUser() != null ?
                session.getCurrentUser().getId() : null;

        List<UserProfile> filteredProfiles = allProfiles.stream()
                .filter(profile -> !profile.getUserId().equals(currentUserId))
                .collect(Collectors.toList());

        profilesList.setAll(filteredProfiles);

        int total = session.getTotalProfilesCount(search);
        // Subtract 1 for the current user
        total = Math.max(0, total - 1);

        int start = page * PAGE_SIZE + 1;
        int end = Math.min(start + filteredProfiles.size() - 1, total);

        if (total == 0) {
            totalUsersLabel.setText("No other users found");
        } else {
            totalUsersLabel.setText(String.format("Showing %d-%d of %d users", start, end, total));
        }
    }

    /**
     * Update pagination control
     */
    private void updatePagination() {
        String search = searchField.getText();
        int total = session.getTotalProfilesCount(search);
        // Subtract 1 for current user
        total = Math.max(0, total - 1);
        int pageCount = (int) Math.ceil((double) total / PAGE_SIZE);
        pagination.setPageCount(Math.max(1, pageCount));
    }

    /**
     * Get sort by value for DAO
     */
    private String getSortByValue() {
        String selected = sortByCombo.getValue();
        if (selected == null) return "name";
        switch (selected) {
            case "First Name": return "firstname";
            case "Last Name": return "lastname";
            default: return "name";
        }
    }

    /**
     * Handle search action
     */
    @FXML
    private void handleSearch() {
        loadProfiles();
    }

    /**
     * Handle clear filters action
     */
    @FXML
    private void handleClearFilters() {
        searchField.clear();
        sortByCombo.setValue("Name");
        loadProfiles();
    }
}
