# Content Service API

<details>
  <summary><strong>Table of Contents</strong></summary>

  * [Query](#query)
  * [Mutation](#mutation)
  * [Objects](#objects)
    * [AssessmentMetadata](#assessmentmetadata)
    * [AssignmentAssessment](#assignmentassessment)
    * [CompositeProgressInformation](#compositeprogressinformation)
    * [ContentMetadata](#contentmetadata)
    * [ContentMutation](#contentmutation)
    * [ContentPayload](#contentpayload)
    * [FlashcardSetAssessment](#flashcardsetassessment)
    * [Item](#item)
    * [ItemProgress](#itemprogress)
    * [MediaContent](#mediacontent)
    * [PaginationInfo](#paginationinfo)
    * [ProgressLogItem](#progresslogitem)
    * [QuizAssessment](#quizassessment)
    * [Section](#section)
    * [SectionMutation](#sectionmutation)
    * [Skill](#skill)
    * [Stage](#stage)
    * [Suggestion](#suggestion)
    * [UserProgressData](#userprogressdata)
  * [Inputs](#inputs)
    * [AssessmentMetadataInput](#assessmentmetadatainput)
    * [CreateAssessmentInput](#createassessmentinput)
    * [CreateContentMetadataInput](#createcontentmetadatainput)
    * [CreateMediaContentInput](#createmediacontentinput)
    * [CreateSectionInput](#createsectioninput)
    * [CreateStageInput](#createstageinput)
    * [DateTimeFilter](#datetimefilter)
    * [IntFilter](#intfilter)
    * [ItemInput](#iteminput)
    * [Pagination](#pagination)
    * [SkillInput](#skillinput)
    * [StringFilter](#stringfilter)
    * [UpdateAssessmentInput](#updateassessmentinput)
    * [UpdateContentMetadataInput](#updatecontentmetadatainput)
    * [UpdateMediaContentInput](#updatemediacontentinput)
    * [UpdateStageInput](#updatestageinput)
  * [Enums](#enums)
    * [BloomLevel](#bloomlevel)
    * [ContentType](#contenttype)
    * [SkillType](#skilltype)
    * [SortDirection](#sortdirection)
    * [SuggestionType](#suggestiontype)
  * [Scalars](#scalars)
    * [Boolean](#boolean)
    * [Date](#date)
    * [DateTime](#datetime)
    * [Float](#float)
    * [Int](#int)
    * [LocalTime](#localtime)
    * [String](#string)
    * [Time](#time)
    * [UUID](#uuid)
    * [Url](#url)
  * [Interfaces](#interfaces)
    * [Assessment](#assessment)
    * [Content](#content)

</details>

## Query
<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="query.contentsbycourseids">contentsByCourseIds</strong></td>
<td valign="top">[[<a href="#content">Content</a>!]!]</td>
<td>

Retrieves all existing contents for a given course.
🔒 The user must have access to the courses with the given ids to access their contents, otherwise an error is thrown.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">courseIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query._internal_noauth_contentsbycourseids">_internal_noauth_contentsByCourseIds</strong></td>
<td valign="top">[[<a href="#content">Content</a>!]!]</td>
<td>

Retrieves all existing contents for a given course.
⚠️ This query is only accessible internally in the system and allows the caller to fetch contents without
any permissions check and should not be called without any validation of the caller's permissions. ⚠️

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">courseIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query.contentsbyids">contentsByIds</strong></td>
<td valign="top">[<a href="#content">Content</a>!]!</td>
<td>

Get contents by ids. Throws an error if any of the ids are not found.
🔒 The user must have access to the courses containing the contents with the given ids to access their contents,
otherwise an error is thrown.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">ids</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query.findcontentsbyids">findContentsByIds</strong></td>
<td valign="top">[<a href="#content">Content</a>]!</td>
<td>

Get contents by ids. If any of the given ids are not found, the corresponding element in the result list will be null.
🔒 The user must have access to the courses containing the contents with the given ids, otherwise null is returned
for the respective contents.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">ids</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query.contentsbychapterids">contentsByChapterIds</strong></td>
<td valign="top">[[<a href="#content">Content</a>!]!]!</td>
<td>

Get contents by chapter ids. Returns a list containing sublists, where each sublist contains all contents
associated with that chapter
🔒 The user must have access to the courses containing the chapters with the given ids, otherwise an error is thrown.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">chapterIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query._internal_noauth_contentsbychapterids">_internal_noauth_contentsByChapterIds</strong></td>
<td valign="top">[[<a href="#content">Content</a>!]!]!</td>
<td>

Get contents by chapter ids. Returns a list containing sublists, where each sublist contains all contents
associated with that chapter
⚠️ This query is only accessible internally in the system and allows the caller to fetch sections without
any permissions check and should not be called without any validation of the caller's permissions. ⚠️

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">chapterIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query._internal_noauth_sectionsbychapterids">_internal_noauth_sectionsByChapterIds</strong></td>
<td valign="top">[[<a href="#section">Section</a>!]!]!</td>
<td>

Retrieves all existing sections for multiple chapters.
⚠️ This query is only accessible internally in the system and allows the caller to fetch sections without
any permissions check and should not be called without any validation of the caller's permissions. ⚠️

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">chapterIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query._internal_noauth_progressbychapterids">_internal_noauth_progressByChapterIds</strong></td>
<td valign="top">[<a href="#compositeprogressinformation">CompositeProgressInformation</a>!]!</td>
<td>

Retrieve progress for multiple chapters
⚠️ This query is only accessible internally in the system and allows the caller to fetch chapter progress without
any permissions check and should not be called without any validation of the caller's permissions. ⚠️

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">chapterIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query.suggestionsbychapterids">suggestionsByChapterIds</strong></td>
<td valign="top">[<a href="#suggestion">Suggestion</a>!]!</td>
<td>

Generates user specific suggestions for multiple chapters.

Only content that the user can access will be considered.
The contents will be ranked by suggested date, with the most overdue or most urgent content first.

🔒 The user must have access to the courses containing the chapters with the given ids, otherwise an error is thrown.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">chapterIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td>

The ids of the chapters for which suggestions should be generated.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">amount</td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The amount of suggestions to generate in total.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">skillTypes</td>
<td valign="top">[<a href="#skilltype">SkillType</a>!]!</td>
<td>

Only suggestions for these skill types will be generated.
If no skill types are given, suggestions for all skill types will be generated,
also containing suggestions for media content (which do not have a skill type).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query._internal_noauth_achievableskilltypesbychapterids">_internal_noauth_achievableSkillTypesByChapterIds</strong></td>
<td valign="top">[[<a href="#skilltype">SkillType</a>!]!]!</td>
<td>

Retrieves all skill types that are achievable for the given chapters.
Each chapter will have its own list of skill types (batching query).
⚠️ This query is only accessible internally in the system and allows the caller to fetch without
any permissions check and should not be called without any validation of the caller's permissions. ⚠️

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">chapterIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query._internal_noauth_achievableskillsbychapterids">_internal_noauth_achievableSkillsByChapterIds</strong></td>
<td valign="top">[[<a href="#skill">Skill</a>!]!]!</td>
<td>

Retrieves all skills that are achievable for the given chapters.
Each chapter will have its own list of skills(batching query).
⚠️ This query is only accessible internally in the system and allows the caller to fetch without
any permissions check and should not be called without any validation of the caller's permissions. ⚠️

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">chapterIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query._internal_noauth_achievableskillsbycourseids">_internal_noauth_achievableSkillsByCourseIds</strong></td>
<td valign="top">[[<a href="#skill">Skill</a>!]!]!</td>
<td>

Retrieves all skills that are achievable for the given courses.
Each course will have its own list of skill(batching query).
⚠️ This query is only accessible internally in the system and allows the caller to fetch without
any permissions check and should not be called without any validation of the caller's permissions. ⚠️

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">courseIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query._internal_noauth_contentwithnosectionbychapterids">_internal_noauth_contentWithNoSectionByChapterIds</strong></td>
<td valign="top">[[<a href="#content">Content</a>!]!]!</td>
<td>

Retrieves all Content that is currently not part of any section within chapters.
⚠️ This query is only accessible internally in the system and allows the caller to fetch content without
any permissions check and should not be called without any validation of the caller's permissions. ⚠️

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">chapterIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="query._internal_noauth_contentsavailabletobeworkedonbyuserforcourses">_internal_noauth_contentsAvailableToBeWorkedOnByUserForCourses</strong></td>
<td valign="top">[<a href="#content">Content</a>!]!</td>
<td></td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">courseIds</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
</tbody>
</table>

## Mutation
<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="mutation._internal_createmediacontent">_internal_createMediaContent</strong></td>
<td valign="top"><a href="#mediacontent">MediaContent</a>!</td>
<td>

Create new media content
️⚠️ This mutation is only accessible internally in the system ⚠️
🔒 The user must have admin access to the course containing the section to perform this action.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">courseId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#createmediacontentinput">CreateMediaContentInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="mutation._internal_createassessment">_internal_createAssessment</strong></td>
<td valign="top"><a href="#assessment">Assessment</a>!</td>
<td>

Create a new Assessment
⚠️ This mutation is only accessible internally in the system ⚠️
🔒 The user must have admin access to the course containing the section to perform this action.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">courseId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#createassessmentinput">CreateAssessmentInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="mutation.mutatecontent">mutateContent</strong></td>
<td valign="top"><a href="#contentmutation">ContentMutation</a>!</td>
<td>

Modify Content
🔒 The user must have admin access to the course containing the section to perform this action.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">contentId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="mutation._internal_createsection">_internal_createSection</strong></td>
<td valign="top"><a href="#section">Section</a>!</td>
<td>

Create new Section
⚠️ This mutation is only accessible internally in the system ⚠️
🔒 The user must have admin access to the course containing the section to perform this action.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">courseId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#createsectioninput">CreateSectionInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="mutation.mutatesection">mutateSection</strong></td>
<td valign="top"><a href="#sectionmutation">SectionMutation</a>!</td>
<td>

Modify the section with the given id.
🔒 The user must have admin access to the course containing the section to perform this action.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">sectionId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
</tbody>
</table>

## Objects

### AssessmentMetadata

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="assessmentmetadata.skillpoints">skillPoints</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Number of skill points a student receives for completing this content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assessmentmetadata.skilltypes">skillTypes</strong></td>
<td valign="top">[<a href="#skilltype">SkillType</a>!]!</td>
<td>

Type of the assessment

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assessmentmetadata.initiallearninginterval">initialLearningInterval</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The initial learning interval for the assessment in days.
This is the interval that is applied after the assessment is completed the first time.
Following intervals are calculated based on the previous interval and the user's performance.
If this is null, the assessment will never be scheduled for review, which
is useful for assessments that are not meant to be repeated.

</td>
</tr>
</tbody>
</table>

### AssignmentAssessment

An assignment, assignment related fields are stored in the assignment service.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="assignmentassessment.assessmentmetadata">assessmentMetadata</strong></td>
<td valign="top"><a href="#assessmentmetadata">AssessmentMetadata</a>!</td>
<td>

Assessment metadata

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assignmentassessment.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

ID of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assignmentassessment.metadata">metadata</strong></td>
<td valign="top"><a href="#contentmetadata">ContentMetadata</a>!</td>
<td>

Metadata of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assignmentassessment.userprogressdata">userProgressData</strong></td>
<td valign="top"><a href="#userprogressdata">UserProgressData</a>!</td>
<td>

Progress data of the content for the current user.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assignmentassessment.progressdataforuser">progressDataForUser</strong></td>
<td valign="top"><a href="#userprogressdata">UserProgressData</a>!</td>
<td>

Progress data of the specified user.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">userId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assignmentassessment.items">items</strong></td>
<td valign="top">[<a href="#item">Item</a>!]!</td>
<td>

the items that belong to the Assignment

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assignmentassessment.isavailabletobeworkedon">isAvailableToBeWorkedOn</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

For the current user, returns true if this content could be worked on by the user (i.e. it is not locked), false
if content is not available to be worked on (e.g. because previous stage has not been completed)

</td>
</tr>
</tbody>
</table>

### CompositeProgressInformation

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="compositeprogressinformation.progress">progress</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>

percentage of completedContents/totalContents

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="compositeprogressinformation.completedcontents">completedContents</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

absolut number of completed content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="compositeprogressinformation.totalcontents">totalContents</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

absolut number of total content

</td>
</tr>
</tbody>
</table>

### ContentMetadata

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="contentmetadata.name">name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Name of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="contentmetadata.type">type</strong></td>
<td valign="top"><a href="#contenttype">ContentType</a>!</td>
<td>

Content type

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="contentmetadata.suggesteddate">suggestedDate</strong></td>
<td valign="top"><a href="#datetime">DateTime</a>!</td>
<td>

Suggested date when the content should be done

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="contentmetadata.rewardpoints">rewardPoints</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Number of reward points a student receives for completing this content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="contentmetadata.chapterid">chapterId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

ID of the chapter this content is associated with

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="contentmetadata.courseid">courseId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

ID of the course this content is associated with

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="contentmetadata.tagnames">tagNames</strong></td>
<td valign="top">[<a href="#string">String</a>!]!</td>
<td>

TagNames this content is tagged with

</td>
</tr>
</tbody>
</table>

### ContentMutation

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="contentmutation.contentid">contentId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

Identifier of Content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="contentmutation.updatemediacontent">updateMediaContent</strong></td>
<td valign="top"><a href="#mediacontent">MediaContent</a>!</td>
<td>

Update an existing Content

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#updatemediacontentinput">UpdateMediaContentInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="contentmutation.updateassessment">updateAssessment</strong></td>
<td valign="top"><a href="#assessment">Assessment</a>!</td>
<td>

Update an existing Assessment

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#updateassessmentinput">UpdateAssessmentInput</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="contentmutation.deletecontent">deleteContent</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

Delete an existing Content, throws an error if no Content with the given id exists

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="contentmutation.addtagtocontent">addTagToContent</strong></td>
<td valign="top"><a href="#content">Content</a>!</td>
<td>

Add a tag to an existing content

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">tagName</td>
<td valign="top"><a href="#string">String</a></td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="contentmutation.removetagfromcontent">removeTagFromContent</strong></td>
<td valign="top"><a href="#content">Content</a>!</td>
<td>

Remove a tag from an existing content

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">tagName</td>
<td valign="top"><a href="#string">String</a></td>
<td></td>
</tr>
</tbody>
</table>

### ContentPayload

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="contentpayload.elements">elements</strong></td>
<td valign="top">[<a href="#content">Content</a>!]!</td>
<td>

the contents

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="contentpayload.pageinfo">pageInfo</strong></td>
<td valign="top"><a href="#paginationinfo">PaginationInfo</a>!</td>
<td>

pagination info

</td>
</tr>
</tbody>
</table>

### FlashcardSetAssessment

A set of flashcards, flashcard related fields are stored in the flashcard service.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="flashcardsetassessment.assessmentmetadata">assessmentMetadata</strong></td>
<td valign="top"><a href="#assessmentmetadata">AssessmentMetadata</a>!</td>
<td>

Assessment metadata

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="flashcardsetassessment.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

ID of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="flashcardsetassessment.metadata">metadata</strong></td>
<td valign="top"><a href="#contentmetadata">ContentMetadata</a>!</td>
<td>

Metadata of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="flashcardsetassessment.userprogressdata">userProgressData</strong></td>
<td valign="top"><a href="#userprogressdata">UserProgressData</a>!</td>
<td>

Progress data of the content for the current user.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="flashcardsetassessment.progressdataforuser">progressDataForUser</strong></td>
<td valign="top"><a href="#userprogressdata">UserProgressData</a>!</td>
<td>

Progress data of the specified user.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">userId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="flashcardsetassessment.items">items</strong></td>
<td valign="top">[<a href="#item">Item</a>!]!</td>
<td>

the items that belong to the Flashcard

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="flashcardsetassessment.isavailabletobeworkedon">isAvailableToBeWorkedOn</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

For the current user, returns true if this content could be worked on by the user (i.e. it is not locked), false
if content is not available to be worked on (e.g. because previous stage has not been completed)

</td>
</tr>
</tbody>
</table>

### Item

An item is a part of an assessment. Based on students' performances on items the SkillLevel Service estimates a students knowledge.
An item is something like a question in a quiz, a flashcard of a flashcard set.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="item.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

the id of the item

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="item.associatedskills">associatedSkills</strong></td>
<td valign="top">[<a href="#skill">Skill</a>!]!</td>
<td>

The skills or the competencies the item belongs to.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="item.associatedbloomlevels">associatedBloomLevels</strong></td>
<td valign="top">[<a href="#bloomlevel">BloomLevel</a>!]!</td>
<td>

The Level of Blooms Taxonomy the item belongs to

</td>
</tr>
</tbody>
</table>

### ItemProgress

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="itemprogress.itemid">itemId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

the id of the corresponding item

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="itemprogress.responsecorrectness">responseCorrectness</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>

the correctness of the users response.
Value between 0 and 1 representing the user's correctness on the content item.

</td>
</tr>
</tbody>
</table>

### MediaContent

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="mediacontent.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

ID of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="mediacontent.metadata">metadata</strong></td>
<td valign="top"><a href="#contentmetadata">ContentMetadata</a>!</td>
<td>

Metadata of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="mediacontent.userprogressdata">userProgressData</strong></td>
<td valign="top"><a href="#userprogressdata">UserProgressData</a>!</td>
<td>

Progress data of the content for the current user.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="mediacontent.progressdataforuser">progressDataForUser</strong></td>
<td valign="top"><a href="#userprogressdata">UserProgressData</a>!</td>
<td>

Progress data of the specified user.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">userId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="mediacontent.isavailabletobeworkedon">isAvailableToBeWorkedOn</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

For the current user, returns true if this content could be worked on by the user (i.e. it is not locked), false
if content is not available to be worked on (e.g. because previous stage has not been completed)

</td>
</tr>
</tbody>
</table>

### PaginationInfo

Return type for information about paginated results.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="paginationinfo.page">page</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The current page number.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="paginationinfo.size">size</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The number of elements per page.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="paginationinfo.totalelements">totalElements</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The total number of elements across all pages.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="paginationinfo.totalpages">totalPages</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The total number of pages.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="paginationinfo.hasnext">hasNext</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Whether there is a next page.

</td>
</tr>
</tbody>
</table>

### ProgressLogItem

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="progresslogitem.timestamp">timestamp</strong></td>
<td valign="top"><a href="#datetime">DateTime</a>!</td>
<td>

The date the user completed the content item.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="progresslogitem.success">success</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

Whether the user completed the content item successfully.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="progresslogitem.correctness">correctness</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>

Value between 0 and 1 representing the user's correctness on the content item.
Can be null as some contents cannot provide a meaningful correctness value.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="progresslogitem.hintsused">hintsUsed</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

How many hints the user used to complete the content item.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="progresslogitem.timetocomplete">timeToComplete</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Time in milliseconds it took the user to complete the content item.
Can be null for contents that do not measure completion time.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="progresslogitem.progressperitem">progressPerItem</strong></td>
<td valign="top"><a href="#itemprogress">ItemProgress</a>!</td>
<td>

!OPTIONAL
the items the user has completed and the students' performance on these items
Can be null as some contents don't contains items for assessments

</td>
</tr>
</tbody>
</table>

### QuizAssessment

A quiz, quiz related fields are stored in the quiz service.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="quizassessment.assessmentmetadata">assessmentMetadata</strong></td>
<td valign="top"><a href="#assessmentmetadata">AssessmentMetadata</a>!</td>
<td>

Assessment metadata

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="quizassessment.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

ID of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="quizassessment.metadata">metadata</strong></td>
<td valign="top"><a href="#contentmetadata">ContentMetadata</a>!</td>
<td>

Metadata of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="quizassessment.userprogressdata">userProgressData</strong></td>
<td valign="top"><a href="#userprogressdata">UserProgressData</a>!</td>
<td>

Progress data of the content for the current user.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="quizassessment.progressdataforuser">progressDataForUser</strong></td>
<td valign="top"><a href="#userprogressdata">UserProgressData</a>!</td>
<td>

Progress data of the specified user.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">userId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="quizassessment.items">items</strong></td>
<td valign="top">[<a href="#item">Item</a>!]!</td>
<td>

the items that belong to the Quiz

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="quizassessment.isavailabletobeworkedon">isAvailableToBeWorkedOn</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

For the current user, returns true if this content could be worked on by the user (i.e. it is not locked), false
if content is not available to be worked on (e.g. because previous stage has not been completed)

</td>
</tr>
</tbody>
</table>

### Section

Representation of a Section

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="section.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

Unique identifier of the Section Object

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="section.courseid">courseId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

Id of the Course the Section is located in.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="section.name">name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Name of the Section

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="section.chapterid">chapterId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

Chapter the Section is located in

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="section.stages">stages</strong></td>
<td valign="top">[<a href="#stage">Stage</a>!]!</td>
<td>

List of Stages contained in a Section

</td>
</tr>
</tbody>
</table>

### SectionMutation

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="sectionmutation.sectionid">sectionId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

Identifier of the section

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="sectionmutation.updatesectionname">updateSectionName</strong></td>
<td valign="top"><a href="#section">Section</a>!</td>
<td>

update the name of a Section

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">name</td>
<td valign="top"><a href="#string">String</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="sectionmutation.deletesection">deleteSection</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

delete a Section by ID

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="sectionmutation.createstage">createStage</strong></td>
<td valign="top"><a href="#stage">Stage</a>!</td>
<td>

create new Stage in Section

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#createstageinput">CreateStageInput</a></td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="sectionmutation.updatestage">updateStage</strong></td>
<td valign="top"><a href="#stage">Stage</a>!</td>
<td>

Update Content of Stage

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">input</td>
<td valign="top"><a href="#updatestageinput">UpdateStageInput</a></td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="sectionmutation.deletestage">deleteStage</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

delete Stage by ID

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">id</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="sectionmutation.updatestageorder">updateStageOrder</strong></td>
<td valign="top"><a href="#section">Section</a>!</td>
<td>

update Order of Stages within a Section

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">stages</td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td></td>
</tr>
</tbody>
</table>

### Skill

a skill or compentency.
Something like loops or data structures.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="skill.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

the id of a skill

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="skill.skillname">skillName</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

the name of the skill

</td>
</tr>
</tbody>
</table>

### Stage

Representation of a Stage

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="stage.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

Unique identifier of the Stage Object

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="stage.position">position</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Position of the Stage within the Section

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="stage.requiredcontents">requiredContents</strong></td>
<td valign="top">[<a href="#content">Content</a>!]!</td>
<td>

List of Content that is labeled as required content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="stage.requiredcontentsprogress">requiredContentsProgress</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>

Percentage of User Progress made to required Content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="stage.optionalcontents">optionalContents</strong></td>
<td valign="top">[<a href="#content">Content</a>!]!</td>
<td>

List of Content that is labeled as optional content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="stage.optionalcontentsprogress">optionalContentsProgress</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>

Percentage of Progress made to optional Content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="stage.isavailabletobeworkedon">isAvailableToBeWorkedOn</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

For the current user, returns true if this stage could be worked on by the user (i.e. it is not locked), false
if stage is not available to be worked on (e.g. because previous stage has not been completed)

</td>
</tr>
</tbody>
</table>

### Suggestion

Represents a suggestion for a user to learn new content or review old content.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="suggestion.content">content</strong></td>
<td valign="top"><a href="#content">Content</a>!</td>
<td>

The content that is suggested to the user.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="suggestion.type">type</strong></td>
<td valign="top"><a href="#suggestiontype">SuggestionType</a>!</td>
<td>

The type of suggestion.

</td>
</tr>
</tbody>
</table>

### UserProgressData

Represents a user's progress on a content item.
See https://gits-enpro.readthedocs.io/en/latest/dev-manuals/gamification/userProgress.html

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="userprogressdata.userid">userId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

The user's id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="userprogressdata.contentid">contentId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

The id of the content item.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="userprogressdata.log">log</strong></td>
<td valign="top">[<a href="#progresslogitem">ProgressLogItem</a>]!</td>
<td>

A list of entries each representing the user completing the content item.
Sorted by date in descending order.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="userprogressdata.learninginterval">learningInterval</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The learning interval in days for the content item.
If null, the content item is not scheduled for learning.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="userprogressdata.nextlearndate">nextLearnDate</strong></td>
<td valign="top"><a href="#datetime">DateTime</a></td>
<td>

The next time the content should be learned.
Calculated using the date the user completed the content item and the learning interval.
This is null if the user has not completed the content item once.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="userprogressdata.lastlearndate">lastLearnDate</strong></td>
<td valign="top"><a href="#datetime">DateTime</a></td>
<td>

The last time the content was learned successfully.
This is null if the user has not completed the content item once.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="userprogressdata.islearned">isLearned</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

True if the user has completed the content item at least once successfully.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="userprogressdata.isdueforreview">isDueForReview</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

True if the assessment is due for review.

</td>
</tr>
</tbody>
</table>

## Inputs

### AssessmentMetadataInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="assessmentmetadatainput.skillpoints">skillPoints</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Number of skill points a student receives for completing this content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assessmentmetadatainput.skilltypes">skillTypes</strong></td>
<td valign="top">[<a href="#skilltype">SkillType</a>!]!</td>
<td>

Type of the assessment

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assessmentmetadatainput.initiallearninginterval">initialLearningInterval</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The initial learning interval for the assessment in days.
This is the interval that is applied after the assessment is completed the first time.
Following intervals are calculated based on the previous interval and the user's performance.
If this is null, the assessment will never be scheduled for review, which
is useful for assessments that are not meant to be repeated.

</td>
</tr>
</tbody>
</table>

### CreateAssessmentInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="createassessmentinput.metadata">metadata</strong></td>
<td valign="top"><a href="#createcontentmetadatainput">CreateContentMetadataInput</a>!</td>
<td>

Metadata for the new Content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="createassessmentinput.assessmentmetadata">assessmentMetadata</strong></td>
<td valign="top"><a href="#assessmentmetadatainput">AssessmentMetadataInput</a>!</td>
<td>

Assessment metadata

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="createassessmentinput.items">items</strong></td>
<td valign="top">[<a href="#iteminput">ItemInput</a>!]</td>
<td>

items of the new assessments

</td>
</tr>
</tbody>
</table>

### CreateContentMetadataInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="createcontentmetadatainput.name">name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Name of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="createcontentmetadatainput.type">type</strong></td>
<td valign="top"><a href="#contenttype">ContentType</a>!</td>
<td>

Type of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="createcontentmetadatainput.suggesteddate">suggestedDate</strong></td>
<td valign="top"><a href="#datetime">DateTime</a>!</td>
<td>

Suggested date when the content should be done

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="createcontentmetadatainput.rewardpoints">rewardPoints</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Number of reward points a student receives for completing this content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="createcontentmetadatainput.chapterid">chapterId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

ID of the chapter this content is associated with

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="createcontentmetadatainput.tagnames">tagNames</strong></td>
<td valign="top">[<a href="#string">String</a>!]!</td>
<td>

TagNames this content is tagged with

</td>
</tr>
</tbody>
</table>

### CreateMediaContentInput

Input for creating new media content. Media specific fields are stored in the Media Service.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="createmediacontentinput.metadata">metadata</strong></td>
<td valign="top"><a href="#createcontentmetadatainput">CreateContentMetadataInput</a>!</td>
<td>

Metadata for the new Content

</td>
</tr>
</tbody>
</table>

### CreateSectionInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="createsectioninput.chapterid">chapterId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

Chapter Section will belong to

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="createsectioninput.name">name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

name given to Section

</td>
</tr>
</tbody>
</table>

### CreateStageInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="createstageinput.requiredcontents">requiredContents</strong></td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td>

updated List of UUIDs for content labeled as required in this Stage

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="createstageinput.optionalcontents">optionalContents</strong></td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td>

updated List of UUIDs for content labeled as optional in this Stage

</td>
</tr>
</tbody>
</table>

### DateTimeFilter

Filter for date values.
If multiple filters are specified, they are combined with AND.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="datetimefilter.after">after</strong></td>
<td valign="top"><a href="#datetime">DateTime</a></td>
<td>

If specified, filters for dates after the specified value.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="datetimefilter.before">before</strong></td>
<td valign="top"><a href="#datetime">DateTime</a></td>
<td>

If specified, filters for dates before the specified value.

</td>
</tr>
</tbody>
</table>

### IntFilter

Filter for integer values.
If multiple filters are specified, they are combined with AND.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="intfilter.equals">equals</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

An integer value to match exactly.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="intfilter.greaterthan">greaterThan</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

If specified, filters for values greater than to the specified value.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="intfilter.lessthan">lessThan</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

If specified, filters for values less than to the specified value.

</td>
</tr>
</tbody>
</table>

### ItemInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="iteminput.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a></td>
<td>

the id of the item

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="iteminput.associatedskills">associatedSkills</strong></td>
<td valign="top">[<a href="#skillinput">SkillInput</a>!]!</td>
<td>

The skills or the competencies the item belongs to.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="iteminput.associatedbloomlevels">associatedBloomLevels</strong></td>
<td valign="top">[<a href="#bloomlevel">BloomLevel</a>!]!</td>
<td>

The Level of Blooms Taxonomy the item belongs to

</td>
</tr>
</tbody>
</table>

### Pagination

Specifies the page size and page number for paginated results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="pagination.page">page</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The page number, starting at 0.
If not specified, the default value is 0.
For values greater than 0, the page size must be specified.
If this value is larger than the number of pages, an empty page is returned.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="pagination.size">size</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The number of elements per page.

</td>
</tr>
</tbody>
</table>

### SkillInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="skillinput.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a></td>
<td>

the id of a skill. Field is optional, because not all required skills may exist, if a new item is created. If the id is empty a new skill,
will be created

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="skillinput.skillname">skillName</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

the name of the skill

</td>
</tr>
</tbody>
</table>

### StringFilter

Filter for string values.
If multiple filters are specified, they are combined with AND.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="stringfilter.equals">equals</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

A string value to match exactly.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="stringfilter.contains">contains</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

A string value that must be contained in the field that is being filtered.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="stringfilter.ignorecase">ignoreCase</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

If true, the filter is case-insensitive.

</td>
</tr>
</tbody>
</table>

### UpdateAssessmentInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="updateassessmentinput.metadata">metadata</strong></td>
<td valign="top"><a href="#updatecontentmetadatainput">UpdateContentMetadataInput</a>!</td>
<td>

Metadata for the new Content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="updateassessmentinput.assessmentmetadata">assessmentMetadata</strong></td>
<td valign="top"><a href="#assessmentmetadatainput">AssessmentMetadataInput</a>!</td>
<td>

Assessment metadata

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="updateassessmentinput.items">items</strong></td>
<td valign="top">[<a href="#iteminput">ItemInput</a>!]</td>
<td>

items of the new assessments

</td>
</tr>
</tbody>
</table>

### UpdateContentMetadataInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="updatecontentmetadatainput.name">name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Name of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="updatecontentmetadatainput.suggesteddate">suggestedDate</strong></td>
<td valign="top"><a href="#datetime">DateTime</a>!</td>
<td>

Date when the content should be done

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="updatecontentmetadatainput.rewardpoints">rewardPoints</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Number of reward points a student receives for completing this content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="updatecontentmetadatainput.chapterid">chapterId</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

ID of the chapter this content is associated with

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="updatecontentmetadatainput.tagnames">tagNames</strong></td>
<td valign="top">[<a href="#string">String</a>!]!</td>
<td>

TagNames this content is tagged with

</td>
</tr>
</tbody>
</table>

### UpdateMediaContentInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="updatemediacontentinput.metadata">metadata</strong></td>
<td valign="top"><a href="#updatecontentmetadatainput">UpdateContentMetadataInput</a>!</td>
<td>

Metadata for the new Content

</td>
</tr>
</tbody>
</table>

### UpdateStageInput

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="updatestageinput.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

Identifier of the Stage

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="updatestageinput.requiredcontents">requiredContents</strong></td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td>

updated List of UUIDs for content labeled as required in this Stage

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="updatestageinput.optionalcontents">optionalContents</strong></td>
<td valign="top">[<a href="#uuid">UUID</a>!]!</td>
<td>

updated List of UUIDs for content labeled as optional in this Stage

</td>
</tr>
</tbody>
</table>

## Enums

### BloomLevel

Level of Blooms Taxonomy

<table>
<thead>
<tr>
<th align="left">Value</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td valign="top"><strong>REMEMBER</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>UNDERSTAND</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>APPLY</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>ANALYZE</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>EVALUATE</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>CREATE</strong></td>
<td></td>
</tr>
</tbody>
</table>

### ContentType

Type of the content

<table>
<thead>
<tr>
<th align="left">Value</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td valign="top"><strong>MEDIA</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>FLASHCARDS</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>QUIZ</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>ASSIGNMENT</strong></td>
<td></td>
</tr>
</tbody>
</table>

### SkillType

Type of the assessment

<table>
<thead>
<tr>
<th align="left">Value</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td valign="top"><strong>CREATE</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>EVALUATE</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>REMEMBER</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>UNDERSTAND</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>APPLY</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>ANALYZE</strong></td>
<td></td>
</tr>
</tbody>
</table>

### SortDirection

Specifies the sort direction, either ascending or descending.

<table>
<thead>
<tr>
<th align="left">Value</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td valign="top"><strong>ASC</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>DESC</strong></td>
<td></td>
</tr>
</tbody>
</table>

### SuggestionType

<table>
<thead>
<tr>
<th align="left">Value</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td valign="top"><strong>NEW_CONTENT</strong></td>
<td></td>
</tr>
<tr>
<td valign="top"><strong>REPETITION</strong></td>
<td></td>
</tr>
</tbody>
</table>

## Scalars

### Boolean

The `Boolean` scalar type represents `true` or `false`.

### Date

### DateTime

### Float

The `Float` scalar type represents signed double-precision fractional values as specified by [IEEE 754](https://en.wikipedia.org/wiki/IEEE_floating_point).

### Int

The `Int` scalar type represents non-fractional signed whole numeric values. Int can represent values between -(2^31) and 2^31 - 1.

### LocalTime

### String

The `String` scalar type represents textual data, represented as UTF-8 character sequences. The String type is most often used by GraphQL to represent free-form human-readable text.

### Time

### UUID

### Url


## Interfaces


### Assessment

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="assessment.assessmentmetadata">assessmentMetadata</strong></td>
<td valign="top"><a href="#assessmentmetadata">AssessmentMetadata</a>!</td>
<td>

Assessment metadata

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assessment.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

ID of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assessment.metadata">metadata</strong></td>
<td valign="top"><a href="#contentmetadata">ContentMetadata</a>!</td>
<td>

Metadata of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assessment.userprogressdata">userProgressData</strong></td>
<td valign="top"><a href="#userprogressdata">UserProgressData</a>!</td>
<td>

Progress data of the content for the current user.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assessment.progressdataforuser">progressDataForUser</strong></td>
<td valign="top"><a href="#userprogressdata">UserProgressData</a>!</td>
<td>

Progress data of the specified user.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">userId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assessment.items">items</strong></td>
<td valign="top">[<a href="#item">Item</a>!]!</td>
<td>

the items that belong to the Assessment

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="assessment.isavailabletobeworkedon">isAvailableToBeWorkedOn</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

For the current user, returns true if this content could be worked on by the user (i.e. it is not locked), false
if content is not available to be worked on (e.g. because previous stage has not been completed)

</td>
</tr>
</tbody>
</table>

**Possible Types:** [FlashcardSetAssessment](#flashcardsetassessment), [QuizAssessment](#quizassessment), [AssignmentAssessment](#assignmentassessment)

### Content

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong id="content.id">id</strong></td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td>

ID of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="content.metadata">metadata</strong></td>
<td valign="top"><a href="#contentmetadata">ContentMetadata</a>!</td>
<td>

Metadata of the content

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="content.userprogressdata">userProgressData</strong></td>
<td valign="top"><a href="#userprogressdata">UserProgressData</a>!</td>
<td>

Progress data of the content for the current user.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="content.progressdataforuser">progressDataForUser</strong></td>
<td valign="top"><a href="#userprogressdata">UserProgressData</a>!</td>
<td>

Progress data of the specified user.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">userId</td>
<td valign="top"><a href="#uuid">UUID</a>!</td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong id="content.isavailabletobeworkedon">isAvailableToBeWorkedOn</strong></td>
<td valign="top"><a href="#boolean">Boolean</a>!</td>
<td>

For the current user, returns true if this content could be worked on by the user (i.e. it is not locked), false
if content is not available to be worked on (e.g. because previous stage has not been completed)

</td>
</tr>
</tbody>
</table>

**Possible Types:** [MediaContent](#mediacontent), [FlashcardSetAssessment](#flashcardsetassessment), [QuizAssessment](#quizassessment), [AssignmentAssessment](#assignmentassessment)
