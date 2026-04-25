const form = document.getElementById("student-form");
const tableBody = document.getElementById("student-table-body");
const messageBox = document.getElementById("message");
const formTitle = document.getElementById("form-title");
const studentCount = document.getElementById("student-count");
const refreshButton = document.getElementById("refresh-button");
const resetButton = document.getElementById("reset-button");
const hiddenIdInput = document.getElementById("student-id");

const fields = {
    studentNo: document.getElementById("studentNo"),
    name: document.getElementById("name"),
    age: document.getElementById("age"),
    major: document.getElementById("major"),
    email: document.getElementById("email")
};

async function request(url, options = {}) {
    const response = await fetch(url, {
        headers: {
            "Content-Type": "application/json"
        },
        ...options
    });

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        const message = data?.message || "请求失败，请稍后重试";
        throw new Error(message);
    }

    return data;
}

function showMessage(message, type = "success") {
    messageBox.textContent = message;
    messageBox.className = `message ${type}`;
}

function clearMessage() {
    messageBox.textContent = "";
    messageBox.className = "message";
}

function resetForm() {
    form.reset();
    hiddenIdInput.value = "";
    formTitle.textContent = "新增学生";
}

function fillForm(student) {
    hiddenIdInput.value = student.id;
    fields.studentNo.value = student.studentNo;
    fields.name.value = student.name;
    fields.age.value = student.age;
    fields.major.value = student.major;
    fields.email.value = student.email;
    formTitle.textContent = `编辑学生 #${student.id}`;
    fields.studentNo.focus();
}

function createTextCell(value) {
    const cell = document.createElement("td");
    cell.textContent = String(value);
    return cell;
}

function createActionButton(action, id, label, type) {
    const button = document.createElement("button");
    button.className = `action-button ${type}`;
    button.type = "button";
    button.dataset.action = action;
    button.dataset.id = String(id);
    button.textContent = label;
    return button;
}

function renderRows(students) {
    studentCount.textContent = students.length;

    if (students.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">当前还没有学生数据，请先新增一条记录。</td>
            </tr>
        `;
        return;
    }

    tableBody.replaceChildren();

    const fragment = document.createDocumentFragment();
    students.forEach((student) => {
        const row = document.createElement("tr");
        row.appendChild(createTextCell(student.studentNo));
        row.appendChild(createTextCell(student.name));
        row.appendChild(createTextCell(student.age));
        row.appendChild(createTextCell(student.major));
        row.appendChild(createTextCell(student.email));

        const actionCell = document.createElement("td");
        const actionWrapper = document.createElement("div");
        actionWrapper.className = "row-actions";
        actionWrapper.appendChild(createActionButton("edit", student.id, "编辑", "edit"));
        actionWrapper.appendChild(createActionButton("delete", student.id, "删除", "delete"));
        actionCell.appendChild(actionWrapper);
        row.appendChild(actionCell);

        fragment.appendChild(row);
    });

    tableBody.appendChild(fragment);
}

async function loadStudents() {
    try {
        const students = await request("/api/students");
        renderRows(students);
        clearMessage();
    } catch (error) {
        showMessage(error.message, "error");
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-state">加载失败，请稍后重试。</td>
            </tr>
        `;
    }
}

async function handleSubmit(event) {
    event.preventDefault();

    const payload = {
        studentNo: fields.studentNo.value,
        name: fields.name.value,
        age: Number(fields.age.value),
        major: fields.major.value,
        email: fields.email.value
    };

    const id = hiddenIdInput.value;
    const url = id ? `/api/students/${id}` : "/api/students";
    const method = id ? "PUT" : "POST";

    try {
        await request(url, {
            method,
            body: JSON.stringify(payload)
        });
        resetForm();
        await loadStudents();
        showMessage(id ? "学生信息已更新" : "学生已成功创建");
    } catch (error) {
        showMessage(error.message, "error");
    }
}

async function handleTableClick(event) {
    const action = event.target.dataset.action;
    const id = event.target.dataset.id;

    if (!action || !id) {
        return;
    }

    if (action === "edit") {
        try {
            const students = await request("/api/students");
            const student = students.find((item) => String(item.id) === id);
            if (!student) {
                throw new Error("学生不存在，可能已被删除");
            }
            fillForm(student);
            clearMessage();
        } catch (error) {
            showMessage(error.message, "error");
        }
        return;
    }

    if (action === "delete") {
        const shouldDelete = window.confirm("确定要删除这条学生记录吗？");
        if (!shouldDelete) {
            return;
        }

        try {
            await request(`/api/students/${id}`, {
                method: "DELETE"
            });
            if (hiddenIdInput.value === id) {
                resetForm();
            }
            await loadStudents();
            showMessage("学生已删除");
        } catch (error) {
            showMessage(error.message, "error");
        }
    }
}

form.addEventListener("submit", handleSubmit);
tableBody.addEventListener("click", handleTableClick);
refreshButton.addEventListener("click", loadStudents);
resetButton.addEventListener("click", () => {
    resetForm();
    clearMessage();
});

loadStudents();
