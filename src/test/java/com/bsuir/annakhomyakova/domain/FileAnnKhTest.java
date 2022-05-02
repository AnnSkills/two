package com.bsuir.annakhomyakova.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bsuir.annakhomyakova.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FileAnnKhTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileAnnKh.class);
        FileAnnKh fileAnnKh1 = new FileAnnKh();
        fileAnnKh1.setId(1L);
        FileAnnKh fileAnnKh2 = new FileAnnKh();
        fileAnnKh2.setId(fileAnnKh1.getId());
        assertThat(fileAnnKh1).isEqualTo(fileAnnKh2);
        fileAnnKh2.setId(2L);
        assertThat(fileAnnKh1).isNotEqualTo(fileAnnKh2);
        fileAnnKh1.setId(null);
        assertThat(fileAnnKh1).isNotEqualTo(fileAnnKh2);
    }
}
