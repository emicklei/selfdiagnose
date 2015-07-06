package com.philemonworks.selfdiagnose;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

public class CollectionIteratorTask extends DiagnosticTask {
    private static final long serialVersionUID = -6424385910785821011L;

    private ArrayList<DiagnosticTask> tasks = new ArrayList<DiagnosticTask>();

    private String expression = "";

    /**
     * This method is sent from the SelfDiagnoseHandler when a configuration is being processed. Use the passed
     * attributes to initialize the receiver.
     * If a variable parameter is passed then store it.
     * 
     * @param attributes
     *        org.xml.sax.Attributes
     */
    public void initializeFromAttributes(Attributes attributes) {
        super.initializeFromAttributes(attributes);
        this.setExpression(attributes.getValue("value"));
    }

    public String getDescription() {
        return "Iterates over elements from the collection value";
    }

    public DiagnosticTaskResult createResult() {
        return new CompositeDiagnosticTaskResult(this);
    }

    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        Object collection = ctx.resolveValue(expression);
        if (collection == null) {
            result.setErrorMessage("Expression [" + expression + "] does not evaluate to a collection;  found null instead");
            return;
        }
        if (!canBeIterated(collection)) {
            result.setErrorMessage("Expression does not evaluate to a collection;  found [" + collection + "] instead");
            return;
        }
        int count = 0;
        if (!(result instanceof CompositeDiagnosticTaskResult))
            throw new RuntimeException("bug");
        CompositeDiagnosticTaskResult composedResult = (CompositeDiagnosticTaskResult) result;
        // either a List or an Array
        if (collection.getClass().isArray()) {
            Object[] array = (Object[]) collection;
            for (int i = 0; i < array.length; i++) {
                this.iterateUsingEach(ctx, composedResult, array[i]);
                count = count + tasks.size();
            }
        } else if (collection instanceof List) {
            List<?> list = (List<?>) collection;
            for (int i = 0; i < list.size(); i++) {
                this.iterateUsingEach(ctx, composedResult, list.get(i));
                count = count + tasks.size();
            }
        }
        int failed = composedResult.howManyErrors();
        if (failed > 0) {
            result.setFailedMessage(failed + " out of " + count + " checks failed.");
        } else {
            result.setPassedMessage("All " + count + " checks passed");
        }
    }

    private void iterateUsingEach(ExecutionContext ctx, CompositeDiagnosticTaskResult composedResult, Object each) throws DiagnoseException {
        for (int t = 0; t < tasks.size(); t++) {
            DiagnosticTask eachTask = (DiagnosticTask) tasks.get(t);
            ctx.setValue(this.getVariableName(), each);
            DiagnosticTaskResult eachResult = eachTask.createResult();
            eachTask.run(ctx, eachResult);
            composedResult.addResult(eachResult);
        }
    }

    private boolean canBeIterated(Object value) {
        return value.getClass().isArray() || value instanceof java.util.List;
    }

    public void register(DiagnosticTask task) {
        tasks.add(task);
    }

    public List<DiagnosticTask> getTasks() {
        return tasks;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
