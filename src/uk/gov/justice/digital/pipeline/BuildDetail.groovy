package uk.gov.justice.digital.pipeline

class BuildDetail implements Serializable {

  private final def script

    BuildDetail(def script) {
    this.script = script
  }

  void setBuildDescription(Map args) {
    script.currentBuild.displayName = args.title
    script.currentBuild.description = args.description
  }

}

