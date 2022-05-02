package com.bsuir.annakhomyakova.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bsuir.annakhomyakova.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskAnnKhTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskAnnKh.class);
        TaskAnnKh taskAnnKh1 = new TaskAnnKh();
        taskAnnKh1.setId(1L);
        TaskAnnKh taskAnnKh2 = new TaskAnnKh();
        taskAnnKh2.setId(taskAnnKh1.getId());
        assertThat(taskAnnKh1).isEqualTo(taskAnnKh2);
        taskAnnKh2.setId(2L);
        assertThat(taskAnnKh1).isNotEqualTo(taskAnnKh2);
        taskAnnKh1.setId(null);
        assertThat(taskAnnKh1).isNotEqualTo(taskAnnKh2);
    }
}
