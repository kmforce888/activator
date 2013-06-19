import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.api.{Git => PGit}
import sbt._

class GitRepository(val repo: Repository) {
  val porcelain = new PGit(repo)

  def headCommit = Option(repo.resolve("HEAD")) map (_.name)
  
  
  def currentTags: Seq[String] = {
    import collection.JavaConverters._
    val list = porcelain.tagList.call().asScala
    for {
      hash <- headCommit.toSeq
      tag <- list
      taghash = tag.getObjectId.getName
      if taghash == hash
      ref = tag.getName
      if ref startsWith "refs/tags/"
    } yield ref drop 10
  }
}
object jgit {
  /** Creates a new git instance from a base directory. */
  def apply(base: File) = new GitRepository({
    val gitDir = new File(base, ".git")
    new FileRepositoryBuilder().setGitDir(gitDir)
      .readEnvironment() // scan environment GIT_* variables
     .findGitDir() // scan up the file system tree
     .build()
  })  
}


