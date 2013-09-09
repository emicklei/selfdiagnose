package com.philemonworks.selfdiagnose;

public class TaskReference extends DiagnosticTask {
    private static final long serialVersionUID = 8262081211708859367L;

    String reference;
    
    public TaskReference(String value) {
        this.reference = value;
    }
    @Override
    public String getDescription() {
        return "stand-in for another task";
    }
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {}
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
}
