/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.descriptor.spi;

import java.util.List;
import java.util.Map;

import org.jboss.shrinkwrap.descriptor.spi.query.NodeQuery;
import org.jboss.shrinkwrap.descriptor.spi.query.Query;
import org.junit.Assert;
import org.junit.Test;

/**
 * NodeTestCase
 * 
 * 
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class NodeTestCase
{
   private static final String ROOT_NAME = "test_root";
   private static final String CHILD_1_NAME = "test_child_1";
   private static final String CHILD_2_NAME = "test_child_2";

   private static final String ATTR_NAME = "test_attr_name";
   private static final String ATTR_VALUE = "test_attr_value";

   private static final String BODY = "test_body";

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionIfNullNameParamInConstructor() throws Exception
   {
      Node parent = new Node(ROOT_NAME);
      new Node(null, parent);
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionIfSpaceInConstructorNameParam() throws Exception
   {
      Node parent = new Node(ROOT_NAME);
      new Node("a name", parent);
   }

   @Test
   public void shouldBeAbleToGetParentNode() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      Node child = root.create(CHILD_1_NAME);

      Assert.assertEquals(
               "Verify ability to get parent node",
               root, child.parent());
   }

   @Test
   public void shouldBeAbleToGetOrCreateExistingNode() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      Node child1 = root.getOrCreate(CHILD_1_NAME);
      Node child1_ref = root.getOrCreate(CHILD_1_NAME);

      Assert.assertEquals(
               "Verify root only has one child",
               1, root.children().size());

      Assert.assertEquals(
            "Verify the previous created node was returned",
            child1, child1_ref);
   }

   @Test
   public void shouldBeAbleToCreateMultipleEquallyNamedChildren() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      Node child1 = root.create(CHILD_1_NAME);
      Node child2 = root.create(CHILD_1_NAME);

      Assert.assertEquals(
               "Verify root only has two children",
               2, root.children().size());

      Assert.assertNotSame(
            "Verify the children are not the same object",
            child1, child2);
   }

   @Test
   public void shouldBeAbleToGetChildNodesByName() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      Node child1 = root.create(CHILD_1_NAME);
      Node child2 = root.create(CHILD_1_NAME);
      root.create(CHILD_2_NAME);

      List<Node> found = root.get(CHILD_1_NAME);

      Assert.assertEquals(
               "Verify only the named nodes were found",
               2, found.size());

      Assert.assertEquals(
               "Verify the correct node was found",
               child1, found.get(0));

      Assert.assertEquals(
               "Verify the correct node was found",
               child2, found.get(1));
   }

   @Test
   public void shouldBeAbleToGetASingleNode() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      Node child = root.create(CHILD_1_NAME);

      Node found = root.getSingle(CHILD_1_NAME);

      Assert.assertEquals(
            "Verify correct node was found",
            child, found);
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionIfMultipleNamedNodesFoundOnGetSingle() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      root.create(CHILD_1_NAME);
      root.create(CHILD_1_NAME);

      // throws Exception, multiple nodes with same name
      root.getSingle(CHILD_1_NAME);
   }

   @Test
   public void shouldBeAbleToReadAndWriteAttribute() throws Exception
   {
      Node root = new Node(ROOT_NAME)
               .attribute(ATTR_NAME, ATTR_VALUE);

      Assert.assertEquals(
               "Verify abillity to store attribues",
               root.getAttribute(ATTR_NAME), ATTR_VALUE);
   }

   @Test
   public void shouldBeAbleToReadAndWriteAttributeObject() throws Exception
   {
      Node root = new Node(ROOT_NAME)
               .attribute(ATTR_NAME, new StringBuilder(ATTR_VALUE));

      Assert.assertEquals(
               "Verify abillity to store attribues",
               root.getAttribute(ATTR_NAME), ATTR_VALUE);
   }

   @Test
   public void shouldBeAbleToReadWriteTextBody() throws Exception
   {
      Node root = new Node(ROOT_NAME)
               .text(BODY);

      Assert.assertEquals(
               "Verify abillity to store text body",
               BODY, root.text());
   }

   @Test
   public void shouldBeAbleToReadWriteTextBodyObject() throws Exception
   {
      Node root = new Node(ROOT_NAME)
               .text(new StringBuilder(BODY));

      Assert.assertEquals(
               "Verify abillity to store text body",
               BODY, root.text());
   }

   @Test
   public void shouldBeAbleToReadAllChildTextBodyValues() throws Exception
   {
      Node root = new Node(ROOT_NAME);

      for (int i = 0; i < 10; i++)
      {
         root.create("subject").text(i);
      }

      List<String> textValues = root.textValues("subject");
      for (int i = 0; i < 10; i++)
      {
         Assert.assertTrue(textValues.contains(String.valueOf(i)));
      }
   }

   @Test
   public void shouldBeAbleToDetermineTextValue() throws Exception
   {
      String childName = "testval";
      String childText = "textval";

      Node root = new Node(ROOT_NAME);
      Assert.assertNull(root.textValue(childName));

      root.create(childName);
      Assert.assertNull(root.textValue(childName));
      
      root.children().get(0).text(childText);
      Assert.assertNotNull(root.textValue(childName));
      Assert.assertEquals(childText, root.textValue(childName));
   }

   @Test
   public void shouldReturnEmptyListForMissingTextValues() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      root.create("child1");
      root.create("child2");
      root.create("child3").text(null);
      List<String> textValues = root.textValues("textValue");
      Assert.assertNotNull(textValues);
      Assert.assertTrue(textValues.isEmpty());
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionIfMultipleChildrenWithSameNameOnTextValue() throws Exception
   {
      String childName = "child";
      Node root = new Node(ROOT_NAME);
      Assert.assertNull(root.textValue(childName));

      root.create(childName);
      root.create(childName);
      root.children().get(0).text("text");
      root.children().get(1).text("text");

      root.textValue(childName);
   }

   @Test
   public void shouldFindAllPropertiesInToString() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      Assert.assertTrue(root.toString().contains(root.getClass().getSimpleName()));
      Assert.assertTrue(root.toString().contains("children"));
      Assert.assertTrue(root.toString().contains("attributes"));
      Assert.assertFalse(root.toString().contains("text"));

      root.text("arbitrary cdata");
      Assert.assertTrue(root.toString().contains("Node"));
      Assert.assertTrue(root.toString().contains("children"));
      Assert.assertTrue(root.toString().contains("attributes"));
      Assert.assertTrue(root.toString().contains("text"));
      Assert.assertTrue(root.toString().contains(root.text()));
   }

   @Test
   public void assertToStringFormat() throws Exception {
      Node root = new Node(ROOT_NAME);
      String r = root.toString();
      Assert.assertTrue(r.startsWith(root.getClass().getSimpleName()));
      Assert.assertTrue(r.indexOf("text") < r.indexOf("Node"));
      Assert.assertTrue(r.indexOf("Node") < r.indexOf("children"));
      Assert.assertTrue(r.indexOf("children") < r.indexOf("attributes"));

      root.text("arbitrary cdata");
      r = root.toString();
      Assert.assertTrue(r.indexOf("Node") < r.indexOf("text"));
      Assert.assertTrue(r.indexOf("Node") < r.indexOf("children"));
      Assert.assertTrue(r.indexOf("children") < r.indexOf("attributes"));
      Assert.assertTrue(r.indexOf("attributes") < r.indexOf("text"));

      Assert.assertTrue("Unexpected content? " + root.toString(), root.toString().contains("children[0]"));
      root.create("testchild1");
      root.create("testchild2");
      Assert.assertTrue("Unexpected content? " + root.toString(), root.toString().contains("children[2]"));

      Assert.assertTrue("Unexpected content? " + root.toString(), root.toString().contains("attributes[{}]"));
      root.attribute("name", "value");
      Assert.assertTrue("Unexpected content? " + root.toString(), root.toString().contains("attributes[{name=value}]"));

      Assert.assertTrue("Unexpected content? " + root.toString(), root.toString().contains("text[arbitrary cdata]"));
   }

   @Test(expected = UnsupportedOperationException.class)
   public void shouldHaveImmutableAttributeMap() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      root.attribute("attribute1", "value");
      root.attribute("attribute2", "value");
      Map<String, String> attributes = root.getAttributes();
      attributes.clear();
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionForNullStringParameter() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      root.remove((String) null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionForEmptyStringParameter() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      root.remove("");
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionForNullQueryParameter() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      root.remove((Query) null);
   }

   @Test
   public void shouldRemoveNodeByString() throws Exception
   {
      String name = "child";
      Node root = new Node(ROOT_NAME);

      Assert.assertTrue(root.children().isEmpty());
      root.create(name);
      Assert.assertFalse(root.children().isEmpty());
      Assert.assertEquals(1, root.children().size());

      root.remove(name);
      Assert.assertTrue(root.children().isEmpty());
   }

   @Test
   public void shouldRemoveSingleChildNodeWithNodeParam()
   {
      Node root = new Node(ROOT_NAME);
      Node child = root.create("child_node");
      Assert.assertTrue(root.removeSingle(child));
   }

   @Test
   public void shouldNotRemoveSingleChildNodeWithNodeParam()
   {
      Node root = new Node(ROOT_NAME);
      Node child = new Node("another_node");
      Assert.assertFalse(root.removeSingle((Node) null));
      Assert.assertFalse(root.removeSingle(child));
      root.create("a_proper_child_node");
      Assert.assertFalse(root.removeSingle(child));
   }

   @Test
   public void shouldRemoveSingleChildNodeWithStringParam()
   {
      Node root = new Node(ROOT_NAME);
      String childNodeName = "another_node";
      Assert.assertNull(root.removeSingle(childNodeName));
      root.create(childNodeName);
      Node removedChild = root.removeSingle(childNodeName);
      Assert.assertNotNull(removedChild);
      Assert.assertEquals(childNodeName, removedChild.name());
   }

   @Test
   public void shouldNotRemoveSingleChildNodeWithStringParam()
   {
      Node root = new Node(ROOT_NAME);
      Assert.assertNull(root.removeSingle("node_that_doesn't_exist"));
      root.create("a_node");
      Assert.assertNull(root.removeSingle("nonexisting_node"));
   }

   @Test
   public void shouldRemoveWithQueryParam() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      Node child = root.create("child_node");

      Assert.assertFalse(root.children().isEmpty());
      Assert.assertEquals(child, root.children().get(0));

      NodeQuery nodeQuery = new NodeQuery(child.name());
      Query query = new Query(false);
      query.addDefinition(nodeQuery);
      List<Node> removedNodes = root.remove(query);
      Assert.assertNotNull(removedNodes);
      Assert.assertFalse(removedNodes.isEmpty());
      Assert.assertEquals(1, removedNodes.size());
   }

   @Test
   public void shouldRemoveWithAbsoluteQueryParam() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      Node child = root.create("child_node");

      Assert.assertFalse(root.children().isEmpty());
      Assert.assertEquals(child, root.children().get(0));

      NodeQuery nodeQuery = new NodeQuery(child.name());
      Query query = new Query(true);
      query.addDefinition(nodeQuery);
      List<Node> removedNodes = root.remove(query);
      Assert.assertNotNull(removedNodes);
      Assert.assertTrue(removedNodes.isEmpty());
   }

   @Test
   public void shouldNotRemoveWithQueryParam() throws Exception
   {
      Node root = new Node(ROOT_NAME);
      Node child = root.create("child_node");

      Assert.assertFalse(root.children().isEmpty());
      Assert.assertEquals(child, root.children().get(0));

      NodeQuery nodeQuery = new NodeQuery("some_other_name");
      Query query = new Query(false);
      query.addDefinition(nodeQuery);
      List<Node> removedNodes = root.remove(query);
      Assert.assertTrue(removedNodes.isEmpty());
   }

}
