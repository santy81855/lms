-- Environmental Science lesson content upgrade
-- Run this AFTER demo_seed_abundant_fixed.sql.
-- It updates only lessons in the seeded ECO2026 course.

USE lms;

-- What Makes an Ecosystem?
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to explain what an ecosystem is, identify living and nonliving parts of an ecosystem, and describe why interactions matter more than simply listing organisms.

Key idea:
An ecosystem is made up of all the living organisms in an area plus the nonliving parts of the environment that affect them. The living parts are called biotic factors. These include plants, animals, fungi, bacteria, and protists. The nonliving parts are called abiotic factors. These include sunlight, temperature, water, soil, rocks, air, nutrients, and climate.

A pond ecosystem is a useful example. The biotic factors might include algae, aquatic plants, insects, fish, frogs, bacteria, and birds. The abiotic factors include the water temperature, the amount of dissolved oxygen, the depth of the pond, sunlight reaching the water, and minerals in the mud. The pond is not just a collection of objects. It is a system because the parts interact. Algae use sunlight and nutrients to grow. Insects may eat algae. Fish may eat insects. Bacteria break down dead material and return nutrients to the water.

Why interactions matter:
A small change in one part of an ecosystem can affect many other parts. For example, if fertilizer runoff enters a pond, algae may grow very quickly. At first, that may seem good for organisms that eat algae. But when the algae die, decomposers break them down and use oxygen in the process. If oxygen levels drop too low, fish and other animals may die. This is why environmental scientists study relationships, not just individual species.

Important vocabulary:
- Biotic factor: a living or once-living part of an ecosystem.
- Abiotic factor: a nonliving physical or chemical part of an ecosystem.
- Population: all members of one species in an area.
- Community: all populations of different species living in an area.
- Ecosystem: a community plus the abiotic environment around it.

Common mistake:
A common mistake is saying that an ecosystem is only the animals and plants. The abiotic parts are just as important. Without sunlight, water, temperature ranges, soil, and nutrients, the living community could not survive.

Practice:
Choose a familiar ecosystem, such as a park, beach, forest, backyard, or city block. List at least five biotic factors and five abiotic factors. Then write three sentences explaining how one biotic factor depends on one abiotic factor.',
        l.estimated_minutes = 35
WHERE c.join_code = 'ECO2026'
  AND l.title = 'What Makes an Ecosystem?';

-- Food Chains, Food Webs, and Energy Pyramids
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to compare food chains and food webs, identify trophic levels, and explain why energy decreases as it moves through an ecosystem.

Key idea:
Energy enters most ecosystems through producers. Producers, such as plants, algae, and some bacteria, make their own food using sunlight or chemical energy. In many ecosystems, plants use photosynthesis to convert sunlight, carbon dioxide, and water into sugars. Those sugars store energy that can move through the ecosystem when organisms eat one another.

A food chain shows one simple pathway for energy. For example:
grass -> grasshopper -> frog -> snake -> hawk

This chain starts with grass, a producer. The grasshopper is a primary consumer because it eats the producer. The frog is a secondary consumer because it eats the grasshopper. The snake and hawk are higher-level consumers. A food chain is useful for showing direction, but real ecosystems are more complicated.

A food web shows many connected food chains. A frog might eat grasshoppers, beetles, and flies. A snake might eat frogs, mice, or bird eggs. A hawk might eat snakes, rabbits, or other birds. Because organisms often have more than one food source, food webs are better models of real ecosystems.

Energy pyramids:
An energy pyramid shows how energy decreases at each trophic level. Producers form the base because they capture the original energy. Primary consumers make up the next level, followed by secondary and tertiary consumers. Only a portion of energy transfers from one level to the next. Much of the energy is used for life processes such as movement, growth, repair, and maintaining body temperature. Some energy is lost as heat.

Why this matters:
Because energy decreases at higher trophic levels, ecosystems usually have fewer top predators than producers. A field can support many grasses, fewer insects, fewer frogs, and very few hawks. This pattern helps explain why top predators can be vulnerable when ecosystems are disturbed.

Decomposers:
Decomposers such as fungi and bacteria break down dead organisms and waste. They do not fit neatly into a simple chain because they act on material from every trophic level. They recycle nutrients back into the ecosystem, making those nutrients available to producers again.

Common mistake:
A common mistake is thinking that energy cycles like nutrients do. Nutrients cycle, but energy flows. Energy enters, moves through organisms, and eventually leaves the ecosystem as heat.

Practice:
Draw a food web with at least six organisms. Label each organism as producer, primary consumer, secondary consumer, tertiary consumer, or decomposer. Then explain what might happen if one primary consumer disappeared.',
        l.estimated_minutes = 40
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Food Chains, Food Webs, and Energy Pyramids';

-- Keystone Species and Ecosystem Balance
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to define keystone species, explain trophic cascades, and analyze why some species have a larger effect than their population size might suggest.

Key idea:
A keystone species is a species that has an unusually large effect on its ecosystem. If a keystone species is removed, the structure of the ecosystem can change dramatically. The term comes from architecture. In an arch, the keystone is the stone that helps hold the structure together. In ecology, a keystone species helps hold the ecosystem''s relationships together.

Case study: sea otters and kelp forests
Sea otters are a classic example of a keystone species. In kelp forest ecosystems, sea urchins eat kelp. Sea otters eat sea urchins. When sea otters are present, they keep sea urchin populations under control. This allows kelp forests to grow. Kelp forests provide habitat for fish, invertebrates, and many other organisms.

If sea otters disappear, sea urchin populations can increase rapidly. More sea urchins eat more kelp. Over time, a healthy kelp forest can become an urchin barren, which has much less habitat and biodiversity. This chain reaction is called a trophic cascade.

Trophic cascades:
A trophic cascade happens when a change at one trophic level causes effects across other trophic levels. The change may begin with a predator, herbivore, or producer. In the sea otter example, removing a predator changes herbivore behavior and population size, which then changes producer abundance.

Not all important species are predators:
Some keystone species are predators, but others are ecosystem engineers. Beavers, for example, build dams that create wetlands. These wetlands provide habitat, store water, reduce erosion, and change nutrient flow. Coral species can also be considered ecosystem engineers because reef structures create habitat for many organisms.

Why this matters:
Protecting a keystone species can protect many other species indirectly. However, identifying keystone species can be difficult because ecosystems are complex. Scientists often need long-term data to understand which species have the strongest effects.

Common mistake:
A common mistake is assuming that the most numerous species is always the most important. Keystone species may not be the most abundant. Their importance comes from their role in the network of interactions.

Practice:
Choose one ecosystem engineer or keystone predator. Write a cause-and-effect chain showing what might happen if that species declined sharply.',
        l.estimated_minutes = 40
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Keystone Species and Ecosystem Balance';

-- Biodiversity at Three Levels
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to describe genetic diversity, species diversity, and ecosystem diversity, and explain how biodiversity supports ecosystem resilience.

Key idea:
Biodiversity means the variety of life. It is not just the number of species in an area. Scientists often describe biodiversity at three levels: genetic diversity, species diversity, and ecosystem diversity.

Genetic diversity:
Genetic diversity is the variety of genes within a population or species. For example, a population of wildflowers may include plants with slightly different drought tolerance, disease resistance, root depth, or flowering time. These differences matter because environmental conditions change. If a disease appears, a genetically diverse population is more likely to include some individuals with resistance. Those individuals may survive and reproduce, helping the population continue.

Species diversity:
Species diversity is the variety of species in a community. A forest with many tree, insect, bird, fungus, and mammal species has higher species diversity than a tree plantation with only one tree species. Species diversity includes both richness, which means how many species are present, and evenness, which means how balanced the population sizes are. A community with ten species but one species dominating almost everything may be less balanced than a community where species are more evenly represented.

Ecosystem diversity:
Ecosystem diversity is the variety of ecosystems in a region. A coastal area might include beaches, dunes, salt marshes, forests, and estuaries. Each ecosystem supports different communities and ecological processes. Protecting ecosystem diversity helps protect many habitats at once.

Biodiversity and resilience:
Resilience is the ability of a system to recover after disturbance. Biodiverse ecosystems often have more resilience because multiple species may perform similar roles. For example, if one pollinator species declines, other pollinators may still support plant reproduction. If one plant species suffers during drought, another may survive and continue providing food or habitat.

Why biodiversity is threatened:
Habitat loss, pollution, climate change, invasive species, and overharvesting can reduce biodiversity. Loss of biodiversity can make ecosystems less stable and less able to provide services such as clean water, pollination, soil formation, carbon storage, and food resources.

Common mistake:
A common mistake is treating biodiversity as only a list of species. Species lists are useful, but genetic variation and habitat variety are also major parts of biodiversity.

Practice:
Imagine two farms. Farm A grows only one crop variety. Farm B grows several crop varieties and has hedgerows with native plants. Which farm is likely to be more resilient to pests or drought? Explain using at least two levels of biodiversity.',
        l.estimated_minutes = 40
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Biodiversity at Three Levels';

-- Adaptations and Ecological Niches
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to explain adaptations, distinguish different types of adaptations, and describe how a species'' niche includes more than where it lives.

Key idea:
An adaptation is a heritable trait that helps an organism survive or reproduce in a particular environment. Adaptations are shaped by natural selection over many generations. Individuals with traits that improve survival or reproduction are more likely to pass those traits on.

Types of adaptations:
Structural adaptations are physical features. Examples include a cactus''s spines, a duck''s webbed feet, a bird''s beak shape, or the thick fur of an Arctic fox.

Behavioral adaptations are actions that improve survival or reproduction. Examples include migration, nocturnal activity, courtship displays, and cooperative hunting.

Physiological adaptations are internal body processes. Examples include venom production, temperature regulation, salt balance in marine animals, or the ability of some plants to conserve water.

Ecological niches:
A niche is the role a species plays in an ecosystem. It includes what the species eats, what eats it, when it is active, how it reproduces, where it finds shelter, what conditions it tolerates, and how it interacts with other species. A habitat is where an organism lives. A niche is how it lives.

For example, two bird species may live in the same forest habitat but have different niches. One may feed on insects under bark during the day, while another eats flying insects at dusk. Because they use different resources in different ways, they may avoid direct competition.

Competition and niche overlap:
When two species use the same limited resource, they compete. If their niches overlap too much, one species may outcompete the other. Over time, species may reduce competition through resource partitioning. This means they use different parts of the resource, different areas, or different times of day.

Why this matters:
Understanding niches helps scientists predict how species may respond to environmental change. If a species has a very specialized niche, it may be more vulnerable when conditions change. Generalist species, which can use many resources or habitats, may adapt more easily to disturbance.

Common mistake:
A common mistake is saying that a niche is simply an organism''s home. The home is the habitat. The niche is the organism''s ecological job and lifestyle.

Practice:
Choose an animal and describe its niche. Include its food source, predators, activity pattern, habitat, and at least two adaptations that help it survive.',
        l.estimated_minutes = 45
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Adaptations and Ecological Niches';

-- Invasive Species Case Study
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to explain what makes a species invasive, describe why invasive species can spread quickly, and evaluate prevention and control strategies.

Key idea:
An invasive species is a nonnative species that spreads in a new environment and causes harm. The harm may be ecological, economic, or related to human health. Not every nonnative species becomes invasive. Many introduced species do not spread aggressively or cause major damage. A species becomes invasive when it establishes, reproduces, spreads, and disrupts the new ecosystem.

Why invasive species can succeed:
Invasive species may enter a new ecosystem without their usual predators, parasites, or diseases. Without these natural controls, their populations can grow quickly. Some invasive species reproduce rapidly, tolerate many conditions, or outcompete native species for food, space, sunlight, or nutrients.

Case study: zebra mussels
Zebra mussels are small freshwater mussels originally from Eurasia. They spread to North America through ballast water from ships. Once introduced, they attached to hard surfaces, reproduced quickly, and filtered large amounts of plankton from the water. This changed food availability for native species. Zebra mussels also clogged pipes, damaged boats, and attached to native mussels, making it difficult for native mussels to survive.

Ecological effects:
Invasive species can reduce biodiversity by competing with native species, preying on native species, changing habitats, spreading disease, or altering nutrient cycles. Some invasive plants grow in dense mats that block sunlight from native plants. Some invasive predators attack prey species that have not evolved defenses against them.

Prevention vs. control:
Prevention is usually cheaper and more effective than removal. Prevention strategies include inspecting boats, cleaning hiking gear, regulating imported plants and animals, and monitoring high-risk areas. Once an invasive species is established, control may involve mechanical removal, chemical treatment, biological control, or habitat restoration. Each strategy has tradeoffs. For example, pesticides may harm nontarget species, and biological control must be studied carefully to avoid introducing another problem species.

Common mistake:
A common mistake is assuming that a species is invasive just because it is nonnative. The key issue is whether it spreads and causes harm.

Practice:
Create a short public service announcement for boaters, hikers, gardeners, or pet owners explaining one way they can prevent invasive species from spreading.',
        l.estimated_minutes = 45
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Invasive Species Case Study';

-- Renewable vs. Nonrenewable Resources
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to distinguish renewable and nonrenewable resources, explain why renewability depends on time scale and use rate, and evaluate resource tradeoffs.

Key idea:
A natural resource is something from the environment that humans use. Resources can provide energy, materials, food, water, shelter, or economic value. Environmental science often classifies resources as renewable or nonrenewable, but the distinction is not always as simple as it first appears.

Renewable resources:
A renewable resource can be replaced naturally on a human time scale if it is managed carefully. Examples include sunlight, wind, flowing water, trees, crops, and some fish populations. Renewable does not mean unlimited. A forest can regrow, but if trees are harvested faster than they grow back, the forest is not being used sustainably. A fish population can reproduce, but overfishing can reduce the population faster than it can recover.

Nonrenewable resources:
A nonrenewable resource forms so slowly that it cannot be replaced on a human time scale. Fossil fuels such as coal, oil, and natural gas are nonrenewable because they take millions of years to form. Many minerals are also considered nonrenewable. Once they are extracted and used, they may become harder to recover or reuse.

Energy resources:
Energy choices involve tradeoffs. Fossil fuels provide high energy output and are currently built into many transportation and electricity systems, but burning them releases greenhouse gases and air pollutants. Solar and wind energy produce electricity without direct fuel combustion, but they depend on location, weather, storage, transmission, and materials for infrastructure. Hydropower can provide reliable electricity, but dams can disrupt river ecosystems and fish migration.

Materials and recycling:
Some nonrenewable materials can be recycled. Recycling aluminum, copper, paper, and some plastics can reduce demand for new extraction. However, recycling still requires collection, sorting, energy, and processing. It is useful but not a complete solution by itself.

Sustainable use:
Sustainable resource use means meeting current needs without reducing the ability of future generations to meet their needs. This requires considering the rate of use, environmental impacts, waste, fairness, and long-term availability.

Common mistake:
A common mistake is thinking that renewable automatically means environmentally harmless. Renewable resources can still damage ecosystems if they are used carelessly.

Practice:
Choose one resource, such as timber, freshwater, natural gas, wind, or copper. Classify it as renewable or nonrenewable, then explain two benefits and two concerns related to its use.',
        l.estimated_minutes = 40
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Renewable vs. Nonrenewable Resources';

-- Sustainable Design Decisions
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to explain sustainability tradeoffs, use a decision matrix, and compare solutions using environmental, economic, and social criteria.

Key idea:
Sustainability is not just about choosing the option that sounds the most environmentally friendly. Real decisions often involve tradeoffs. A sustainable design decision considers environmental impacts, cost, reliability, fairness, accessibility, and long-term consequences.

The three dimensions of sustainability:
Environmental criteria focus on impacts such as pollution, habitat loss, carbon emissions, water use, waste, and biodiversity.

Economic criteria focus on cost, maintenance, jobs, efficiency, durability, and whether the solution can realistically be funded and maintained.

Social criteria focus on people. This includes health, safety, access, community needs, fairness, cultural values, and who benefits or bears the burden.

Example problem:
Imagine a school wants to reduce its environmental footprint. Possible solutions include installing solar panels, starting a compost program, replacing old lights with LEDs, planting shade trees, reducing single-use plastics, or improving bus routes.

Each option has strengths and limitations. Solar panels may reduce electricity emissions but require upfront cost. Composting reduces food waste but requires student participation and a plan for collection. LED lighting is often cost-effective but may not teach students much about broader sustainability. Shade trees provide cooling and habitat but take time to grow.

Decision matrix:
A decision matrix helps compare options. List the possible solutions in rows and the criteria in columns. Then score each solution from 1 to 5 for each criterion. You might include categories such as cost, emissions reduction, student involvement, ease of implementation, and long-term benefit. The highest score is not automatically the best choice, but the matrix makes reasoning visible and organized.

Why this matters:
Environmental problems rarely have perfect solutions. A good solution is often one that balances multiple needs, reduces harm, and can actually be implemented. Sustainability requires evidence, creativity, and compromise.

Common mistake:
A common mistake is choosing a solution based only on one factor. A plan that is environmentally strong but impossible to fund may fail. A plan that is cheap but ineffective may also fail.

Practice:
Create a decision matrix for three ways your school or community could reduce waste. Use at least four criteria and explain which option you would recommend.',
        l.estimated_minutes = 45
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Sustainable Design Decisions';

-- Water Use and Conservation
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to describe major categories of water use, explain why freshwater is limited, and evaluate conservation strategies.

Key idea:
Water covers much of Earth, but only a small portion is fresh water available for human use. Much of Earth''s freshwater is frozen in ice or stored underground. Rivers, lakes, wetlands, and groundwater provide drinking water, irrigation, industry, transportation, recreation, and habitat for countless species.

Major water uses:
Agriculture is one of the largest uses of freshwater in many regions. Crops require water, and livestock production also uses water for drinking, feed crops, and processing. Industrial use includes manufacturing, cooling power plants, mining, and producing goods. Household use includes drinking, bathing, cooking, cleaning, toilets, lawns, and gardens.

Water stress:
Water stress happens when demand for water approaches or exceeds available supply. Water stress can result from drought, overuse, pollution, population growth, inefficient infrastructure, and climate change. Some areas may have enough water in total but lack clean water or reliable distribution systems.

Groundwater:
Groundwater is water stored in underground spaces within soil and rock. Aquifers can supply wells and irrigation systems. If groundwater is pumped faster than it is recharged, water levels can fall. In coastal areas, overpumping can allow saltwater to move into freshwater aquifers, making the water less usable.

Conservation strategies:
Water conservation means using water more efficiently and reducing waste. In agriculture, drip irrigation can deliver water directly to plant roots, reducing evaporation and runoff. Choosing crops suited to local climate can also reduce water demand. In cities, fixing leaks, using efficient appliances, collecting rainwater where legal, planting drought-tolerant landscapes, and reusing greywater can reduce demand.

Water quality:
Conservation is not only about quantity. Pollution can make water unsafe or expensive to treat. Fertilizers, pesticides, oil, sewage, industrial chemicals, and sediment can reduce water quality. Protecting wetlands and watersheds helps filter water and reduce flooding.

Common mistake:
A common mistake is thinking water conservation only means shorter showers. Household habits matter, but agriculture, industry, infrastructure, and land management are also major parts of water conservation.

Practice:
Track three ways water is used in a school day. For each use, suggest one realistic conservation strategy and one challenge that might make the strategy difficult.',
        l.estimated_minutes = 40
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Water Use and Conservation';

-- Air and Water Pollution
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to distinguish point-source and nonpoint-source pollution, describe common air and water pollutants, and explain how pollutants move through environmental systems.

Key idea:
Pollution is the introduction of harmful substances or energy into the environment. Pollution can affect air, water, soil, organisms, and human health. Some pollutants are visible, such as trash or smoke. Others are invisible, such as dissolved chemicals, microscopic particles, or excess nutrients.

Point-source pollution:
Point-source pollution comes from a single identifiable location. Examples include a pipe discharging wastewater into a river, smoke from a factory stack, or a leaking storage tank. Because the source is easier to identify, point-source pollution can often be monitored and regulated directly.

Nonpoint-source pollution:
Nonpoint-source pollution comes from many diffuse sources. Examples include fertilizer runoff from lawns and farms, oil washed from roads, sediment from construction sites, and bacteria carried by stormwater. Nonpoint-source pollution is harder to control because it comes from many places and often increases during rainfall or snowmelt.

Water pollution:
Water pollutants include nutrients, pathogens, heavy metals, sediments, plastics, oil, pesticides, and industrial chemicals. Excess nutrients such as nitrogen and phosphorus can cause algal blooms. When algae die and decompose, oxygen levels may drop, creating dead zones where many aquatic organisms cannot survive. Sediment can cloud water, block sunlight, and smother habitats. Plastics can entangle animals or break into microplastics that enter food webs.

Air pollution:
Air pollutants include particulate matter, nitrogen oxides, sulfur dioxide, carbon monoxide, ozone near the ground, and volatile organic compounds. Some pollutants are released directly. Others form through chemical reactions in the atmosphere. Ground-level ozone, for example, can form when sunlight reacts with pollutants from vehicles and industry.

Pollution movement:
Pollutants move through air and water. A chemical released upstream can affect ecosystems downstream. Air pollutants can travel across regions before settling. Some pollutants accumulate in organisms. Bioaccumulation occurs when a pollutant builds up in an organism over time. Biomagnification occurs when pollutant concentrations increase at higher trophic levels.

Common mistake:
A common mistake is thinking pollution only matters near the source. Because air and water move, pollution can affect places far from where it began.

Practice:
Identify one likely point source and one likely nonpoint source of pollution in your community. Explain how each pollutant could move through the environment.',
        l.estimated_minutes = 45
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Air and Water Pollution';

-- Evidence for Climate Change
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to describe several lines of evidence for climate change and explain why scientists rely on patterns from multiple data sources.

Key idea:
Climate describes long-term patterns in temperature, precipitation, wind, seasons, and extreme events. Weather is what happens over short periods, such as a rainy day or a hot week. Climate change refers to long-term changes in climate patterns. Scientists study climate using many independent lines of evidence.

Temperature records:
Instrumental temperature records from weather stations, ships, buoys, and satellites show that global average temperature has increased over time. Scientists do not rely on one thermometer or one location. They combine many measurements, correct for known issues, and look for large-scale patterns.

Ice cores:
Ice cores are cylinders of ice drilled from glaciers and ice sheets. Layers in the ice can preserve information about past climates. Tiny air bubbles trapped in the ice contain samples of ancient atmosphere. By analyzing these bubbles, scientists can estimate past concentrations of greenhouse gases such as carbon dioxide and methane. Ice cores also contain clues about past temperature and volcanic activity.

Sea level:
Sea level is rising for two major reasons. First, water expands as it warms. Second, melting land ice from glaciers and ice sheets adds water to the ocean. Sea level data comes from tide gauges and satellites. Rising seas can increase coastal flooding, erosion, saltwater intrusion, and storm surge impacts.

Glaciers and snow cover:
Many glaciers around the world are shrinking. Snow cover and seasonal ice patterns are changing in many regions. These changes affect water supplies, ecosystems, and communities that depend on meltwater.

Phenology:
Phenology is the timing of seasonal biological events, such as flowering, migration, breeding, and leaf-out. In some places, plants are flowering earlier or animals are shifting migration timing. These changes can disrupt relationships, such as when pollinators and flowering plants no longer match as well as before.

Why multiple evidence sources matter:
Any single dataset can have uncertainty, but confidence increases when many independent sources point in the same direction. Temperature records, ice cores, sea level measurements, glacier observations, ocean heat data, and phenology all help build a consistent picture.

Common mistake:
A common mistake is confusing a cold day with evidence against climate change. Weather varies from day to day. Climate change is about long-term patterns across many places and many years.

Practice:
Choose two types of climate evidence and explain what each one measures. Then explain why using both together gives a stronger conclusion than using only one.',
        l.estimated_minutes = 50
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Evidence for Climate Change';

-- Mitigation and Adaptation Strategies
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to distinguish mitigation from adaptation and evaluate examples of each strategy.

Key idea:
Climate responses fall into two broad categories: mitigation and adaptation. Mitigation means reducing the causes of climate change, especially greenhouse gas emissions. Adaptation means reducing harm from climate impacts that are already happening or expected to happen.

Mitigation:
Mitigation strategies reduce emissions or increase carbon storage. Examples include improving energy efficiency, using renewable electricity, electrifying transportation, improving public transit, protecting forests, restoring wetlands, reducing food waste, changing industrial processes, and designing buildings that use less energy.

Some mitigation strategies are individual, such as using less energy at home or choosing lower-emission transportation when possible. Others require community, business, or government action, such as changing electricity systems, improving building codes, or expanding transit networks.

Adaptation:
Adaptation strategies help people and ecosystems cope with changes. Examples include building flood defenses, restoring wetlands that absorb storm surge, planting urban trees to reduce heat, designing cooling centers, changing crop varieties, improving water storage, creating wildfire defensible space, and updating emergency plans.

Adaptation does not solve the root cause of climate change, but it can reduce risk and protect lives, property, food systems, water supplies, and ecosystems.

Comparing strategies:
Many climate strategies can do both mitigation and adaptation. For example, planting urban trees can store some carbon, reduce air temperatures, absorb stormwater, improve air quality, and provide shade. Restoring wetlands can store carbon while also reducing flood damage and supporting biodiversity.

Equity and decision-making:
Climate impacts are not distributed equally. Some communities face greater heat exposure, flood risk, pollution, or limited access to resources. Good climate planning considers who is most vulnerable and who has the ability to adapt. Fair planning includes community input and avoids shifting burdens onto people with fewer resources.

Common mistake:
A common mistake is treating mitigation and adaptation as competing choices. Most communities need both. Mitigation reduces future damage, while adaptation reduces current and near-term risk.

Practice:
Create a two-column chart. In one column, list three mitigation strategies. In the other, list three adaptation strategies. Then choose one strategy that could do both and explain why.',
        l.estimated_minutes = 45
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Mitigation and Adaptation Strategies';

-- Choosing a Local Environmental Issue
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to identify a local environmental issue, define a focused problem statement, and identify stakeholders who are connected to the issue.

Key idea:
A strong environmental action project begins with a focused problem. Broad topics such as pollution, climate change, or waste are important, but they are too large for a local project unless they are narrowed. A focused problem identifies what is happening, where it is happening, who is affected, and why it matters.

Examples of focused issues:
Instead of saying, "Plastic is bad," a focused issue might be, "Single-use plastic bottles are frequently left around the school athletic field after events, creating litter and increasing cleanup needs."

Instead of saying, "Heat is a problem," a focused issue might be, "The bus stop near the school has no shade, making students wait in high temperatures during late spring and early fall."

Instead of saying, "Water is wasted," a focused issue might be, "The courtyard sprinklers water the sidewalk several mornings a week, wasting water and creating slippery walkways."

Stakeholders:
Stakeholders are people or groups who affect or are affected by the issue. For a school waste project, stakeholders might include students, custodians, teachers, administrators, cafeteria staff, families, and local waste services. For a tree-planting project, stakeholders might include property managers, neighbors, maintenance staff, city officials, and students who use the area.

Evidence:
Before proposing a solution, gather evidence. Evidence might include photos, counts, surveys, interviews, maps, temperature readings, waste audits, or observations over time. Evidence helps show that the problem is real and helps you choose a solution that fits the situation.

Problem statement:
A strong problem statement is specific and neutral. It does not blame people. It describes the condition and its impact. For example: "During lunch, recyclable containers are often placed in trash bins because recycling bins are hard to find and labels are unclear."

Common mistake:
A common mistake is jumping to a solution before understanding the problem. If the problem is unclear, the solution may not work.

Practice:
Choose one local environmental issue. Write a problem statement in one or two sentences. Then list at least five stakeholders and two types of evidence you could collect.',
        l.estimated_minutes = 40
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Choosing a Local Environmental Issue';

-- Designing an Action Plan
UPDATE lessons l
    JOIN course_modules cm ON l.module_id = cm.id
    JOIN courses c ON cm.course_id = c.id
    SET l.content = 'Learning goal:
By the end of this lesson, you should be able to design an environmental action plan with a goal, evidence, steps, constraints, and a way to measure success.

Key idea:
An action plan turns an environmental concern into a realistic project. A good plan is specific enough to guide action but flexible enough to adjust when new information appears. It should explain the problem, the goal, the evidence, the proposed solution, the timeline, the people involved, and how success will be measured.

Parts of an action plan:
1. Problem statement:
Describe the issue clearly. Include where it occurs and who is affected.

2. Evidence:
Summarize the data or observations that show the problem exists. This might include counts, photos, survey responses, interviews, or measurements.

3. Goal:
State what the project hopes to accomplish. A strong goal is measurable. For example: "Reduce visible lunch-area litter by 40 percent over four weeks" is stronger than "Help the environment."

4. Strategy:
Explain what actions will be taken. Strategies might include education, signage, redesigning a space, changing a routine, building a prototype, collecting data, or presenting a recommendation to decision-makers.

5. Stakeholders:
Identify who needs to be involved. This may include people who can approve changes, people who will maintain the project, and people who will use the solution.

6. Constraints:
Every project has constraints. These may include cost, time, permission, safety, materials, maintenance, or school rules. Naming constraints early makes the plan more realistic.

7. Measurement:
Decide how you will know whether the plan worked. Measurements might include before-and-after counts, survey results, participation numbers, temperature readings, or waste weights.

Example:
If the problem is lunch-area litter, the plan might include a one-week litter count, student interviews, clearer bin labels, a short announcement campaign, and a follow-up litter count. Success could be measured by comparing the average number of litter items before and after the intervention.

Common mistake:
A common mistake is writing an action plan that depends on someone else doing all the work. A strong plan identifies what the project team can actually do and what support they need from others.

Practice:
Draft a one-page action plan for the issue you selected in the previous lesson. Include a measurable goal, three action steps, two constraints, and one way to measure success.',
        l.estimated_minutes = 45
WHERE c.join_code = 'ECO2026'
  AND l.title = 'Designing an Action Plan';

SELECT 'Environmental Science lesson upgrade completed.' AS message;
SELECT l.title, CHAR_LENGTH(l.content) AS content_characters, l.estimated_minutes
FROM lessons l
         JOIN course_modules cm ON l.module_id = cm.id
         JOIN courses c ON cm.course_id = c.id
WHERE c.join_code = 'ECO2026'
ORDER BY cm.module_order, l.lesson_order;
