package tests;

class HeapTask {

    Operation operation;
    Integer element;
    Integer priority;

    HeapTask(Operation operation, Integer element, Integer priority) {
        this.operation = operation;
        this.element = element;
        this.priority = priority;
    }
}
