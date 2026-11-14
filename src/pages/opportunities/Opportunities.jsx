
import { useEffect, useState } from "react";
import {
  BriefcaseBusiness,
  Search,
  MapPin,
  Building2,
  CalendarDays,
  CalendarPlus,
  ExternalLink,
  Bookmark,
  CheckCircle,
  Clock,
  Send,
  Filter,
  Plus,
  Trash2,
  X,
  Pencil,
  History,
  ChevronDown,
  ChevronUp,
  StickyNote,
  Save,
  MessageSquare,
} from "lucide-react";

import {
  getAllOpportunities,
  createOpportunity,
  updateOpportunity,
  updateOpportunityStatus,
  deleteOpportunity,
  getOpportunityStatistics,
} from "../../api/opportunityService";

import {
  getOpportunityHistory,
} from "../../api/opportunityHistoryService";

import { createEvent } from "../../api/eventService";
import { formatDate, formatDateTime } from "../../utils/dateTime";
import TimeInput from "../../components/TimeInput";

import {
  createOpportunityNote,
  getOpportunityNotes,
  updateOpportunityNote,
  deleteOpportunityNote,
} from "../../api/opportunityNoteService";

const emptyForm = {
  title: "",
  company: "",
  location: "",
  employmentType: "Full Time",
  description: "",
  applicationUrl: "",
  deadline: "",
};

export default function Opportunities() {
  const [opportunities, setOpportunities] = useState([]);
  const [statistics, setStatistics] = useState({});
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedStatus, setSelectedStatus] = useState("ALL");

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState(emptyForm);

  // Application history state
  const [expandedHistoryId, setExpandedHistoryId] = useState(null);
  const [historyData, setHistoryData] = useState({});
  const [historyLoading, setHistoryLoading] = useState(false);
  const [historyError, setHistoryError] = useState("");

  // Application notes state
  const [expandedNotesId, setExpandedNotesId] = useState(null);
  const [notesData, setNotesData] = useState({});
  const [noteContent, setNoteContent] = useState("");
  const [editingNoteId, setEditingNoteId] = useState(null);
  const [notesLoading, setNotesLoading] = useState(false);
  const [noteSaving, setNoteSaving] = useState(false);
  const [notesError, setNotesError] = useState("");
  const [notesSuccess, setNotesSuccess] = useState("");

  // Career event creation state
  const [expandedEventId, setExpandedEventId] = useState(null);
  const [careerEventDate, setCareerEventDate] = useState("");
  const [careerEventTime, setCareerEventTime] = useState("");
  const [eventSaving, setEventSaving] = useState(false);
  const [eventError, setEventError] = useState("");
  const [eventSuccess, setEventSuccess] = useState("");

  useEffect(() => {
    loadOpportunities();
  }, []);

  const loadOpportunities = async () => {
    try {
      setLoading(true);
      setError("");

      const [opportunityData, statisticsData] = await Promise.all([
        getAllOpportunities(),
        getOpportunityStatistics(),
      ]);

      setOpportunities(opportunityData);
      setStatistics(statisticsData);
    } catch (err) {
      setError(
        err.response?.data?.message ||
        "Unable to load opportunities."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const openCreateForm = () => {
    setEditingId(null);
    setFormData(emptyForm);
    setShowForm(true);
    setError("");
    setSuccess("");
  };

  const openEditForm = (item) => {
    setEditingId(item.id);

    setFormData({
      title: item.title || "",
      company: item.company || "",
      location: item.location || "",
      employmentType: item.employmentType || "Full Time",
      description: item.description || "",
      applicationUrl: item.applicationUrl || "",
      deadline: item.deadline || "",
    });

    setShowForm(true);
    setError("");
    setSuccess("");

    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const closeForm = () => {
    setShowForm(false);
    setEditingId(null);
    setFormData(emptyForm);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");
      setSuccess("");

      const opportunityData = {
        ...formData,
        deadline: formData.deadline || null,
      };

      if (editingId !== null) {
        await updateOpportunity(editingId, opportunityData);
        setSuccess("Opportunity updated successfully.");
      } else {
        await createOpportunity(opportunityData);
        setSuccess("Opportunity added successfully.");
      }

      closeForm();
      await loadOpportunities();
    } catch (err) {
      setError(
        err.response?.data?.message ||
        "Unable to save opportunity."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleStatusChange = async (id, status) => {
    try {
      setError("");
      setSuccess("");

      await updateOpportunityStatus(id, status);

      if (expandedHistoryId === id) {
        const history = await getOpportunityHistory(id);

        setHistoryData((previous) => ({
          ...previous,
          [id]: history,
        }));
      }

      setSuccess("Application status updated successfully.");
      await loadOpportunities();
    } catch (err) {
      setError(
        err.response?.data?.message ||
        "Unable to update application status."
      );
    }
  };

  const toggleSavedStatus = async (item) => {
    const newStatus =
      item.status === "SAVED" ? "APPLIED" : "SAVED";

    await handleStatusChange(item.id, newStatus);
  };

  const handleDeleteOpportunity = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this opportunity?"
    );

    if (!confirmed) return;

    try {
      setError("");
      setSuccess("");

      await deleteOpportunity(id);

      setHistoryData((previous) => {
        const updated = { ...previous };
        delete updated[id];
        return updated;
      });

      setNotesData((previous) => {
        const updated = { ...previous };
        delete updated[id];
        return updated;
      });

      if (expandedHistoryId === id) {
        setExpandedHistoryId(null);
      }

      if (expandedNotesId === id) {
        setExpandedNotesId(null);
      }

      setSuccess("Opportunity deleted successfully.");
      await loadOpportunities();
    } catch (err) {
      setError(
        err.response?.data?.message ||
        "Unable to delete opportunity."
      );
    }
  };

  // Open or close the create-career-event form
  const toggleCareerEventForm = (item) => {
    if (expandedEventId === item.id) {
      setExpandedEventId(null);
      setCareerEventDate("");
      setCareerEventTime("");
      setEventError("");
      setEventSuccess("");
      return;
    }

    setExpandedEventId(item.id);
    setCareerEventDate("");
    setCareerEventTime("");
    setEventError("");
    setEventSuccess("");
  };

  // Create a regular Opportunity360 event
  const handleCreateCareerEvent = async (event, item) => {
    event.preventDefault();

    if (!careerEventDate) {
      setEventError("Please select an event date.");
      return;
    }

    try {
      setEventSaving(true);
      setEventError("");
      setEventSuccess("");

      const eventData = {
        title: `Career: ${item.title} - ${item.company}`.slice(0, 150),
        description: [
          `Career follow-up for opportunity: ${item.title} at ${item.company}.`,
          item.location ? `Location: ${item.location}.` : "",
          item.applicationUrl ? `Application link: ${item.applicationUrl}` : "",
          item.description || "",
        ].filter(Boolean).join("\\n\\n").slice(0, 1000),
        category: "CAREER",
        eventDate: careerEventDate,
        eventTime: careerEventTime || null,
        timezone: "Asia/Kolkata",
        recurrenceType: "NONE",
        recurrenceEndDate: null,
      };

      await createEvent(eventData);

      setEventSuccess(
        "Career event created successfully. You can manage its reminders in the Reminders section."
      );

      setCareerEventDate("");
      setCareerEventTime("");
    } catch (err) {
      setEventError(
        err.response?.data?.message ||
        "Unable to create the career event."
      );
    } finally {
      setEventSaving(false);
    }
  };

  // Open or close application history
  const toggleHistory = async (id) => {
    if (expandedHistoryId === id) {
      setExpandedHistoryId(null);
      setHistoryError("");
      return;
    }

    setExpandedHistoryId(id);
    setHistoryError("");

    if (historyData[id]) {
      return;
    }

    try {
      setHistoryLoading(true);

      const history = await getOpportunityHistory(id);

      setHistoryData((previous) => ({
        ...previous,
        [id]: history,
      }));
    } catch (err) {
      setHistoryError(
        err.response?.data?.message ||
        "Unable to load application history."
      );
    } finally {
      setHistoryLoading(false);
    }
  };

  // Open or close application notes
  const toggleNotes = async (id) => {
    if (expandedNotesId === id) {
      setExpandedNotesId(null);
      setNotesError("");
      setNotesSuccess("");
      setEditingNoteId(null);
      setNoteContent("");
      return;
    }

    setExpandedNotesId(id);
    setNotesError("");
    setNotesSuccess("");
    setEditingNoteId(null);
    setNoteContent("");

    try {
      setNotesLoading(true);

      const notes = await getOpportunityNotes(id);

      setNotesData((previous) => ({
        ...previous,
        [id]: notes,
      }));
    } catch (err) {
      setNotesError(
        err.response?.data?.message ||
        "Unable to load application notes."
      );
    } finally {
      setNotesLoading(false);
    }
  };

  // Add or update an application note
  const handleNoteSubmit = async (event, opportunityId) => {
    event.preventDefault();

    if (!noteContent.trim()) {
      setNotesError("Please enter your note.");
      return;
    }

    try {
      setNoteSaving(true);
      setNotesError("");
      setNotesSuccess("");

      if (editingNoteId !== null) {
        await updateOpportunityNote(
          opportunityId,
          editingNoteId,
          noteContent.trim()
        );

        setNotesSuccess("Note updated successfully.");
      } else {
        await createOpportunityNote(
          opportunityId,
          noteContent.trim()
        );

        setNotesSuccess("Note added successfully.");
      }

      const updatedNotes = await getOpportunityNotes(opportunityId);

      setNotesData((previous) => ({
        ...previous,
        [opportunityId]: updatedNotes,
      }));

      setNoteContent("");
      setEditingNoteId(null);
    } catch (err) {
      setNotesError(
        err.response?.data?.message ||
        "Unable to save note."
      );
    } finally {
      setNoteSaving(false);
    }
  };

  // Start editing a note
  const handleEditNote = (note) => {
    setEditingNoteId(note.id);
    setNoteContent(note.content);
    setNotesError("");
    setNotesSuccess("");
  };

  // Cancel note editing
  const cancelNoteEdit = () => {
    setEditingNoteId(null);
    setNoteContent("");
    setNotesError("");
  };

  // Delete an application note
  const handleDeleteNote = async (opportunityId, noteId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this note?"
    );

    if (!confirmed) return;

    try {
      setNotesError("");
      setNotesSuccess("");

      await deleteOpportunityNote(opportunityId, noteId);

      const updatedNotes = await getOpportunityNotes(opportunityId);

      setNotesData((previous) => ({
        ...previous,
        [opportunityId]: updatedNotes,
      }));

      if (editingNoteId === noteId) {
        setEditingNoteId(null);
        setNoteContent("");
      }

      setNotesSuccess("Note deleted successfully.");
    } catch (err) {
      setNotesError(
        err.response?.data?.message ||
        "Unable to delete note."
      );
    }
  };

  const formatStatus = (status) =>
    status?.replaceAll("_", " ") || "";

  const filteredOpportunities = opportunities.filter((item) => {
    const search = searchTerm.toLowerCase();

    const matchesSearch =
      item.title?.toLowerCase().includes(search) ||
      item.company?.toLowerCase().includes(search) ||
      item.location?.toLowerCase().includes(search);

    const matchesStatus =
      selectedStatus === "ALL" || item.status === selectedStatus;

    return matchesSearch && matchesStatus;
  });

  return (
    <div className="opportunities-page">

      {/* Header */}
      <div className="opportunities-header">
        <div>
          <h1>Opportunities</h1>
          <p>
            Discover opportunities and manage your career journey.
          </p>
        </div>

        <button
          className="primary-button"
          onClick={showForm ? closeForm : openCreateForm}
        >
          {showForm ? <X size={16} /> : <Plus size={16} />}
          {showForm ? "Close Form" : "Add Opportunity"}
        </button>
      </div>

      {error && <div className="auth-error">{error}</div>}
      {success && <div className="auth-success">{success}</div>}

      {/* Create and Edit Form */}
      {showForm && (
        <div className="opportunity-form-card">
          <h2>
            {editingId !== null
              ? "Edit Opportunity"
              : "Add New Opportunity"}
          </h2>

          <form onSubmit={handleSubmit}>
            <div className="opportunity-form-grid">
              <input
                type="text"
                name="title"
                placeholder="Job Title"
                value={formData.title}
                onChange={handleInputChange}
                maxLength={150}
                required
              />

              <input
                type="text"
                name="company"
                placeholder="Company Name"
                value={formData.company}
                onChange={handleInputChange}
                maxLength={150}
                required
              />

              <input
                type="text"
                name="location"
                placeholder="Location"
                value={formData.location}
                onChange={handleInputChange}
              />

              <select
                name="employmentType"
                value={formData.employmentType}
                onChange={handleInputChange}
              >
                <option value="Full Time">Full Time</option>
                <option value="Part Time">Part Time</option>
                <option value="Internship">Internship</option>
                <option value="Contract">Contract</option>
              </select>

              <input
                type="url"
                name="applicationUrl"
                placeholder="Application URL (optional)"
                value={formData.applicationUrl}
                onChange={handleInputChange}
              />

              <input
                type="date"
                name="deadline"
                value={formData.deadline}
                onChange={handleInputChange}
              />
            </div>

            <textarea
              name="description"
              placeholder="Job Description"
              value={formData.description}
              onChange={handleInputChange}
              rows={4}
            />

            <div className="opportunity-form-actions">
              <button
                type="submit"
                className="primary-button"
                disabled={saving}
              >
                {saving
                  ? "Saving..."
                  : editingId !== null
                    ? "Update Opportunity"
                    : "Save Opportunity"}
              </button>

              <button
                type="button"
                className="secondary-button"
                onClick={closeForm}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Summary Cards */}
      <div className="opportunity-summary-grid">
        <div className="opportunity-summary-card">
          <div className="opportunity-summary-icon purple">
            <BriefcaseBusiness size={20} />
          </div>
          <p>Total Opportunities</p>
          <h2>{loading ? "—" : statistics.totalOpportunities ?? 0}</h2>
        </div>

        <div className="opportunity-summary-card">
          <div className="opportunity-summary-icon blue">
            <Send size={20} />
          </div>
          <p>Applied</p>
          <h2>{loading ? "—" : statistics.applied ?? 0}</h2>
        </div>

        <div className="opportunity-summary-card">
          <div className="opportunity-summary-icon orange">
            <Clock size={20} />
          </div>
          <p>In Progress</p>
          <h2>{loading ? "—" : statistics.inProgress ?? 0}</h2>
        </div>

        <div className="opportunity-summary-card">
          <div className="opportunity-summary-icon green">
            <CheckCircle size={20} />
          </div>
          <p>Selected</p>
          <h2>{loading ? "—" : statistics.selected ?? 0}</h2>
        </div>
      </div>

      {/* Search and Filter */}
      <div className="opportunities-toolbar">
        <div className="opportunity-search">
          <Search size={17} />
          <input
            type="text"
            placeholder="Search job title, company or location..."
            value={searchTerm}
            onChange={(event) => setSearchTerm(event.target.value)}
          />
        </div>

        <div className="opportunity-filter">
          <Filter size={16} />
          <select
            value={selectedStatus}
            onChange={(event) => setSelectedStatus(event.target.value)}
          >
            <option value="ALL">All Applications</option>
            <option value="SAVED">Saved</option>
            <option value="APPLIED">Applied</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="SELECTED">Selected</option>
            <option value="REJECTED">Rejected</option>
            <option value="CLOSED">Closed</option>
          </select>
        </div>
      </div>

      {/* Opportunity Cards */}
      <div className="opportunities-list">
        {loading ? (
          <div className="opportunities-empty-state">
            Loading opportunities...
          </div>
        ) : filteredOpportunities.length > 0 ? (
          filteredOpportunities.map((item) => (
            <div className="opportunity-card" key={item.id}>
              <div className="opportunity-company-icon">
                <Building2 size={23} />
              </div>

              <div className="opportunity-card-content">
                <div className="opportunity-card-heading">
                  <div>
                    <h2>{item.title}</h2>
                    <p>{item.company}</p>
                  </div>

                  <span
                    className={`opportunity-status ${item.status.toLowerCase()}`}
                  >
                    {formatStatus(item.status)}
                  </span>
                </div>

                <p className="opportunity-description">
                  {item.description || "No description available."}
                </p>

                <div className="opportunity-details">
                  <span>
                    <MapPin size={14} />
                    {item.location || "Location not specified"}
                  </span>

                  <span>
                    <BriefcaseBusiness size={14} />
                    {item.employmentType || "Not specified"}
                  </span>

                  <span>
                    <CalendarDays size={14} />
                    {item.deadline ? formatDate(item.deadline) : "No deadline"}
                  </span>
                </div>

                <div className="opportunity-card-actions">
                  <select
                    value={item.status}
                    onChange={(event) =>
                      handleStatusChange(item.id, event.target.value)
                    }
                    aria-label={`Update status for ${item.title}`}
                  >
                    <option value="SAVED">Saved</option>
                    <option value="APPLIED">Applied</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="SELECTED">Selected</option>
                    <option value="REJECTED">Rejected</option>
                    <option value="CLOSED">Closed</option>
                  </select>

                  <button
                    className="opportunity-save-button"
                    onClick={() => toggleSavedStatus(item)}
                  >
                    <Bookmark size={15} />
                    {item.status === "SAVED" ? "Saved" : "Save"}
                  </button>

                  <button
                    className="opportunity-save-button"
                    onClick={() => openEditForm(item)}
                  >
                    <Pencil size={15} />
                    Edit
                  </button>

                  {item.applicationUrl && (
                    <a
                      href={item.applicationUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="opportunity-view-button"
                    >
                      View Job <ExternalLink size={14} />
                    </a>
                  )}

                  <button
                    className="opportunity-save-button"
                    onClick={() => toggleCareerEventForm(item)}
                  >
                    <CalendarPlus size={15} />
                    Create Event
                    {expandedEventId === item.id
                      ? <ChevronUp size={14} />
                      : <ChevronDown size={14} />}
                  </button>

                  <button
                    className="opportunity-save-button"
                    onClick={() => toggleHistory(item.id)}
                  >
                    <History size={15} />
                    {expandedHistoryId === item.id
                      ? "Hide History"
                      : "View History"}

                    {expandedHistoryId === item.id
                      ? <ChevronUp size={14} />
                      : <ChevronDown size={14} />}
                  </button>

                  <button
                    className="opportunity-save-button"
                    onClick={() => toggleNotes(item.id)}
                  >
                    <StickyNote size={15} />
                    {expandedNotesId === item.id
                      ? "Hide Notes"
                      : "Application Notes"}

                    {expandedNotesId === item.id
                      ? <ChevronUp size={14} />
                      : <ChevronDown size={14} />}
                  </button>

                  <button
                    className="opportunity-save-button"
                    onClick={() => handleDeleteOpportunity(item.id)}
                  >
                    <Trash2 size={15} />
                    Delete
                  </button>
                </div>

                {/* Create Career Event */}
                {expandedEventId === item.id && (
                  <div className="opportunity-notes-panel">
                    <div className="opportunity-notes-heading">
                      <h3>
                        <CalendarPlus size={18} />
                        Create Career Event
                      </h3>
                    </div>

                    {eventError && (
                      <div className="auth-error">
                        {eventError}
                      </div>
                    )}

                    {eventSuccess && (
                      <div className="auth-success">
                        {eventSuccess}
                      </div>
                    )}

                    <form
                      className="opportunity-note-form"
                      onSubmit={(event) => handleCreateCareerEvent(event, item)}
                    >
                      <div className="opportunity-form-grid">
                        <input
                          type="text"
                          value={`Career: ${item.title} - ${item.company}`.slice(0, 150)}
                          readOnly
                          aria-label="Event title"
                        />

                        <input
                          type="date"
                          value={careerEventDate}
                          onChange={(event) => setCareerEventDate(event.target.value)}
                          min={new Date().toLocaleDateString("en-CA")}
                          required
                          aria-label="Event date"
                        />

                        <TimeInput
                          value={careerEventTime}
                          onChange={setCareerEventTime}
                        />
                      </div>

                      <div className="opportunity-note-form-actions">
                        <button
                          type="submit"
                          className="primary-button"
                          disabled={eventSaving || !careerEventDate}
                        >
                          <CalendarPlus size={15} />
                          {eventSaving ? "Creating Event..." : "Create Career Event"}
                        </button>

                        <button
                          type="button"
                          className="secondary-button"
                          onClick={() => toggleCareerEventForm(item)}
                        >
                          Close
                        </button>
                      </div>
                    </form>
                  </div>
                )}

                {/* Application History */}
                {expandedHistoryId === item.id && (
                  <div className="opportunity-history-panel">
                    <h3>Application History</h3>

                    {historyLoading ? (
                      <p>Loading history...</p>
                    ) : historyError ? (
                      <p className="auth-error">{historyError}</p>
                    ) : (historyData[item.id] || []).length === 0 ? (
                      <p>No application history available.</p>
                    ) : (
                      <div className="opportunity-history-list">
                        {historyData[item.id].map((history) => (
                          <div
                            className="opportunity-history-item"
                            key={history.id}
                          >
                            <div className="opportunity-history-dot" />

                            <div>
                              <strong>
                                {history.previousStatus
                                  ? `${formatStatus(history.previousStatus)} → `
                                  : ""}
                                {formatStatus(history.newStatus)}
                              </strong>

                              <p>
                                {formatDateTime(history.changedAt)}
                              </p>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                )}

                {/* Application Notes */}
                {expandedNotesId === item.id && (
                  <div className="opportunity-notes-panel">
                    <div className="opportunity-notes-heading">
                      <h3>
                        <MessageSquare size={18} />
                        Application Notes
                      </h3>
                      <span>
                        {(notesData[item.id] || []).length} notes
                      </span>
                    </div>

                    {notesLoading ? (
                      <p>Loading notes...</p>
                    ) : (
                      <>
                        {notesError && (
                          <div className="auth-error">
                            {notesError}
                          </div>
                        )}

                        {notesSuccess && (
                          <div className="auth-success">
                            {notesSuccess}
                          </div>
                        )}

                        <form
                          className="opportunity-note-form"
                          onSubmit={(event) =>
                            handleNoteSubmit(event, item.id)
                          }
                        >
                          <textarea
                            placeholder="Write interview details, HR conversations, follow-up information or other important notes..."
                            value={noteContent}
                            onChange={(event) =>
                              setNoteContent(event.target.value)
                            }
                            rows={4}
                            maxLength={2000}
                            required
                          />

                          <div className="opportunity-note-form-actions">
                            <button
                              type="submit"
                              className="primary-button"
                              disabled={noteSaving || !noteContent.trim()}
                            >
                              {noteSaving ? (
                                "Saving..."
                              ) : editingNoteId !== null ? (
                                <>
                                  <Save size={15} />
                                  Update Note
                                </>
                              ) : (
                                <>
                                  <Plus size={15} />
                                  Add Note
                                </>
                              )}
                            </button>

                            {editingNoteId !== null && (
                              <button
                                type="button"
                                className="secondary-button"
                                onClick={cancelNoteEdit}
                              >
                                Cancel Edit
                              </button>
                            )}
                          </div>
                        </form>

                        <div className="opportunity-notes-list">
                          {(notesData[item.id] || []).length === 0 ? (
                            <div className="opportunity-notes-empty">
                              <StickyNote size={28} />
                              <p>No notes added yet.</p>
                              <span>
                                Add your first note to keep track of this
                                application.
                              </span>
                            </div>
                          ) : (
                            notesData[item.id].map((note) => (
                              <div
                                className="opportunity-note-item"
                                key={note.id}
                              >
                                <div className="opportunity-note-content">
                                  <p>{note.content}</p>

                                  <span>
                                    Created: {formatDateTime(note.createdAt)}
                                  </span>

                                  {note.updatedAt &&
                                    note.updatedAt !== note.createdAt && (
                                      <span>
                                        Updated: {formatDateTime(note.updatedAt)}
                                      </span>
                                    )}
                                </div>

                                <div className="opportunity-note-actions">
                                  <button
                                    type="button"
                                    className="opportunity-save-button"
                                    onClick={() => handleEditNote(note)}
                                    title="Edit note"
                                  >
                                    <Pencil size={14} />
                                    Edit
                                  </button>

                                  <button
                                    type="button"
                                    className="opportunity-save-button"
                                    onClick={() =>
                                      handleDeleteNote(item.id, note.id)
                                    }
                                    title="Delete note"
                                  >
                                    <Trash2 size={14} />
                                    Delete
                                  </button>
                                </div>
                              </div>
                            ))
                          )}
                        </div>
                      </>
                    )}
                  </div>
                )}

              </div>
            </div>
          ))
        ) : (
          <div className="opportunities-empty-state">
            <BriefcaseBusiness size={35} />
            <h3>No opportunities found</h3>
            <p>Try changing your search or application filter.</p>
          </div>
        )}
      </div>

    </div>
  );
}
