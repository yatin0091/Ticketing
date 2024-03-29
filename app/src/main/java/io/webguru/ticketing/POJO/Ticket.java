package io.webguru.ticketing.POJO;

import android.renderscript.ScriptIntrinsicYuvToRGB;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yatin on 15/10/16.
 */

public class Ticket implements Serializable{

    private String ticketNumber;
    private String status;
    private String priority;
    private String issueImageName;
    private int requesterId;
    private int agentId;
    private int approverId;
    private int contractorId;
    private String dateTime;
    private String searchKeword;
    private RequesterData requester;
    private AgentData agentData;
    private AssignedContractor assignedContractor;
    private AssignedApprover assignedApprover;
    private ContractorData contractorData;
    private String isVisibleContractor;
    private ApprovarData approvarData;
    private WorkCompleted workCompleted;
    private WorkRating workRating;
    private String agent_status;

    private boolean isDetailsShown;

    public Ticket(){

    }

    public Ticket(String ticketNumber, String status, String issueImageName, int requesterId, int agentId,
                  int approverId, int contractorId, String dateTime, RequesterData requesterData,
                  String agent_status, String searchKeword){
        this.ticketNumber = ticketNumber;
        this.status = status;
        this.priority = requesterData.getPriority();
        this.issueImageName = issueImageName;
        this.requesterId =requesterId;
        this.agentId = agentId;
        this.approverId = approverId;
        this.contractorId = contractorId;
        this.dateTime = dateTime;
        this.requester = requesterData;
        this.agent_status = agent_status;
        this.searchKeword = searchKeword;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ticketNumber", ticketNumber);
        result.put("status", status);
        result.put("priority", priority);
        result.put("issueImageName", issueImageName);
        result.put("requesterId", requesterId);
        result.put("agentId", agentId);
        result.put("approverId", approverId);
        result.put("contractorId", contractorId);
        result.put("dateTime", dateTime);
        result.put("searchKeword", searchKeword);
        result.put("requester", requester);
        result.put("agentData", agentData);
        result.put("assignedContractor", assignedContractor);
        result.put("assignedApprover", assignedApprover);
        result.put("contractorData", contractorData);
        result.put("isVisibleContractor", isVisibleContractor);
        result.put("approvarData", approvarData);
        result.put("workCompleted", workCompleted);
        result.put("workRating", workRating);
        result.put("agent_status", agent_status);

        return result;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getIssueImageName() {
        return issueImageName;
    }

    public void setIssueImageName(String issueImageName) {
        this.issueImageName = issueImageName;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public AssignedContractor getAssignedContractor() {
        return assignedContractor;
    }

    public void setAssignedContractor(AssignedContractor assignedContractor) {
        this.assignedContractor = assignedContractor;
    }

    public AssignedApprover getAssignedApprover() {
        return assignedApprover;
    }

    public void setAssignedApprover(AssignedApprover assignedApprover) {
        this.assignedApprover = assignedApprover;
    }

    public int getApproverId() {
        return approverId;
    }

    public void setApproverId(int approverId) {
        this.approverId = approverId;
    }

    public int getContractorId() {
        return contractorId;
    }

    public void setContractorId(int contractorId) {
        this.contractorId = contractorId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSearchKeword() {
        return searchKeword;
    }

    public void setSearchKeword(String searchKeword) {
        this.searchKeword = searchKeword;
    }

    public RequesterData getRequester() {
        return requester;
    }

    public void setRequester(RequesterData requester) {
        this.requester = requester;
    }

    public AgentData getAgentData() {
        return agentData;
    }

    public void setAgentData(AgentData agentData) {
        this.agentData = agentData;
    }

    public ContractorData getContractorData() {
        return contractorData;
    }

    public void setContractorData(ContractorData contractorData) {
        this.contractorData = contractorData;
    }

    public String getIsVisibleContractor() {
        return isVisibleContractor;
    }

    public void setIsVisibleContractor(String isVisibleContractor) {
        this.isVisibleContractor = isVisibleContractor;
    }

    public ApprovarData getApprovarData() {
        return approvarData;
    }

    public void setApprovarData(ApprovarData approvarData) {
        this.approvarData = approvarData;
    }

    public WorkCompleted getWorkCompleted() {
        return workCompleted;
    }

    public void setWorkCompleted(WorkCompleted workCompleted) {
        this.workCompleted = workCompleted;
    }

    public WorkRating getWorkRating() {
        return workRating;
    }

    public void setWorkRating(WorkRating workRating) {
        this.workRating = workRating;
    }

    public String getAgent_status() {
        return agent_status;
    }

    public void setAgent_status(String agent_status) {
        this.agent_status = agent_status;
    }

    public boolean isDetailsShown() {
        return isDetailsShown;
    }

    public void setDetailsShown(boolean detailsShown) {
        isDetailsShown = detailsShown;
    }

}
