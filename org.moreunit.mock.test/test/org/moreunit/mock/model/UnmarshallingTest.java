package org.moreunit.mock.model;

import static org.fest.assertions.Assertions.assertThat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

public class UnmarshallingTest
{
    static Unmarshaller unmarshaller;
    static
    {
        try
        {
            JAXBContext jc = JAXBContext.newInstance(MockingTemplates.class);
            unmarshaller = jc.createUnmarshaller();
        }
        catch (JAXBException e)
        {
            throw new RuntimeException("Could not create unmarshaller", e);
        }
    }

    @Test
    public void should_handle_missing_mocking_templates() throws Exception
    {
        // given
        MockingTemplates templates = (MockingTemplates) unmarshaller.unmarshal(getClass().getResource("no_mocking_template.xml"));
        // when
        templates.iterator();
        // then no NPE is thrown
    }

    @Test
    public void should_handle_missing_categories() throws Exception
    {
        // given
        MockingTemplates templates = (MockingTemplates) unmarshaller.unmarshal(getClass().getResource("no_category.xml"));
        // then
        assertThat(templates.categories()).isNotNull().isEmpty();
    }
}
